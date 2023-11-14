package com.smartcare.SmartCare.Services.Implementation;

import com.smartcare.SmartCare.DTO.NgoWithKms;
import com.smartcare.SmartCare.Kafka.Config.AppConstants;
import com.smartcare.SmartCare.Model.Customer;
import com.smartcare.SmartCare.Model.HelpList;
import com.smartcare.SmartCare.Redis.Model.RedisHelpList;
import com.smartcare.SmartCare.Repository.ActiveAgentRepo;
import com.smartcare.SmartCare.Repository.HelpListRepo;
import com.smartcare.SmartCare.Repository.OwnerRepo;
import com.smartcare.SmartCare.Services.RequestServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.resps.GeoRadiusResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class RequestServicesImpl implements RequestServices {

    private final double rangeForFindingNearestNgo = 1.5;

    @Autowired
    private UnifiedJedis jedis;
    @Autowired
    private OwnerServiceImpl geoSearch;

    @Autowired
    private ActiveAgentRepo activeAgentRepo;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Autowired
    private HelpListRepo helpListRepo;

    @Autowired
    private OwnerRepo ownerRepo;

    @Autowired
    private RedisTemplate redisTemplate;

    private final String hashKeyForRequestSentNgo = "RequestSentNgo";

    private Logger log = LoggerFactory.getLogger(RequestServicesImpl.class);
    @Override
    public List<String> findNearestNgoByLongLat(double longitude, double latitude) {
        List<GeoRadiusResponse> nearestNgo = jedis.geosearch
                (geoSearch.getHashKeyForNgoLocation(),
                        new GeoCoordinate(longitude, latitude),
                        rangeForFindingNearestNgo,
                        GeoUnit.KM);
        return nearestNgo.stream()
                .map(GeoRadiusResponse::getMemberByString)
                .collect(Collectors.toList());
    }

    @Override
    public double calculateDistanceBetweenTwoLongLat(double long1, double lat1, double long2, double lat2) {
        long1 = Math.toRadians(long1);
        long2 = Math.toRadians(long2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dlon = long2 - long1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double r = 6371;
        return (c*r);
    }

    @Override
    public List<NgoWithKms> sentNearestNgoWithDistance(double longitude, double latitude) {
        List<String> nearestNgoByLongLat = findNearestNgoByLongLat(longitude, latitude);
        List<NgoWithKms> finalResultOfNgoWithKms = new ArrayList<>();
        log.info(String.valueOf(nearestNgoByLongLat.size()));
        if(nearestNgoByLongLat.isEmpty()) {
            for (int i = 0; i < nearestNgoByLongLat.size(); i++) {
                NgoWithKms ngoWithKms = new NgoWithKms();
                String ngoId = nearestNgoByLongLat.get(i);
                log.info(ngoId);
                Map<String, String> longLatByNgoId = ownerRepo.findLongLatByNgoId(ngoId);
                String longitude1 = longLatByNgoId.get("longitude");
                String latitude1 = longLatByNgoId.get("latitude");
                double distanceFromUserCurrentLocation = calculateDistanceBetweenTwoLongLat(longitude, latitude, Double.parseDouble(longitude1), Double.parseDouble(latitude1));
                ngoWithKms.setNgoName(ngoId);
                ngoWithKms.setDistance(distanceFromUserCurrentLocation);
                finalResultOfNgoWithKms.add(i, ngoWithKms);
            }
            return finalResultOfNgoWithKms;
        }
        throw new RuntimeException("No Nearest Ngo is found");

    }

    @Override
    public String bookedNgo(String ngoId, String custId,String lon,String lat) {
        int totalActiveNgoMembers = activeAgentRepo.totalActiveNgoMembers(ngoId);
        if(totalActiveNgoMembers > 0){
            RedisHelpList redisHelpList = new RedisHelpList();
            redisHelpList.setRequestDate(new Date());
            redisHelpList.setSolvedTime(null);
            redisHelpList.setLongitude(lon);
            redisHelpList.setLatitude(lat);
            redisHelpList.setStatus("OPEN");
            redisHelpList.setCustomerId(custId);
            redisHelpList.setNgoId(ngoId);
            redisTemplate.opsForHash().put(hashKeyForRequestSentNgo,custId,redisHelpList);
            kafkaTemplate.send(AppConstants.RequestTopicName,custId);
            return "Booked";
        }
        throw new RuntimeException("there is no one available into this ngo..!!");
    }

    @Override
    public Object viewRequestByCustId(String id) {
        return redisTemplate.opsForHash().get(hashKeyForRequestSentNgo,id);
    }

    @Override
    public Object markedRequestAsClosed(String id) {
        RedisHelpList list = (RedisHelpList) redisTemplate.opsForHash().get(hashKeyForRequestSentNgo, id);
        list.setStatus("ClOSED");
        list.setSolvedTime(new Date());
        redisTemplate.opsForHash().put(hashKeyForRequestSentNgo,id,list);

        HelpList helpList = new HelpList();
        helpList.setRequestDate(list.getRequestDate());
        helpList.setLongitude(list.getLongitude());
        helpList.setLatitude(list.getLatitude());
        helpList.setStatus(list.getStatus());
        helpList.setSolvedTime(list.getSolvedTime());
        helpList.setNgoId(list.getNgoId());
        Customer customer = new Customer();
        customer.setUserId(list.getCustomerId());
        helpList.setCustomer(customer);
        helpListRepo.save(helpList);
        redisTemplate.opsForHash().delete(hashKeyForRequestSentNgo,id);
        log.info(String.valueOf(list));
        return null;
    }

    @Override
    public Object allRequest(String type, String id) {
        if(type.equals("Agent") || type.equals("Owner")){
            return helpListRepo.findByngoId(id);
        }
        return helpListRepo.findRequestByUserId(id);
    }

}
