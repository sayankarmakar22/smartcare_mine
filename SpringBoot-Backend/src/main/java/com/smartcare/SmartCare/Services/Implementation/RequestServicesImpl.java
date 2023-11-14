package com.smartcare.SmartCare.Services.Implementation;

import com.smartcare.SmartCare.DTO.NgoWithKms;
import com.smartcare.SmartCare.Repository.OwnerRepo;
import com.smartcare.SmartCare.Services.RequestServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.resps.GeoRadiusResponse;

import java.util.ArrayList;
import java.util.HashMap;
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
    private OwnerRepo ownerRepo;

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
    public String bookedNgo(String ngoId, String custId) {
        return null;
    }

}
