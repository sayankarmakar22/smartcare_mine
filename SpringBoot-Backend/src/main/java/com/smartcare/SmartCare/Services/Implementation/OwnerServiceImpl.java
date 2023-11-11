package com.smartcare.SmartCare.Services.Implementation;

import com.smartcare.SmartCare.DTO.OwnerDTO;
import com.smartcare.SmartCare.Helper.OwnerHelper;
import com.smartcare.SmartCare.Model.Agent;
import com.smartcare.SmartCare.Model.Owner;
import com.smartcare.SmartCare.Redis.Helper.RedisOwnerHelper;
import com.smartcare.SmartCare.Redis.Model.RedisOwner;
import com.smartcare.SmartCare.Repository.OwnerRepo;
import com.smartcare.SmartCare.Services.OwnerServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class OwnerServiceImpl implements OwnerServices {
    @Autowired
    private OwnerRepo ownerRepo;
    @Value("${upload.path}")
    private String pathToSavedAadharCard;

    private final String HashKeyForOwner = "owner";
    private final String HashKeyForNgoLocation = "longlatofngo";

    @Autowired
    private RedisTemplate redisTemplate;

    private Logger log = LoggerFactory.getLogger(OwnerServiceImpl.class);
    @Override
    public Boolean saveAadharCardToLocalStorage(MultipartFile file,String ngoId) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path filePath = Paths.get(pathToSavedAadharCard, ngoId.concat(".jpg"));

        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            outputStream.write(file.getBytes());
            return true;
        }
        catch (Exception e){
        return false;}

    }
    @Override
    public Object saveOwner(OwnerDTO ownerDTO) {
        Owner owner = ownerRepo.save(OwnerHelper.convertIntoOwner(ownerDTO, new Owner()));
        redisTemplate.opsForHash().put(HashKeyForOwner,owner.getOwnerId(), RedisOwnerHelper.convertIntoRedisOwner(new RedisOwner(),owner));
        redisTemplate.opsForGeo().add(HashKeyForNgoLocation,new Point(Double.parseDouble(owner.getLongitude()),Double.parseDouble(owner.getLatitude())),owner.getNgoId());
        log.info("saved to owner to db, ngo id is : " + ownerDTO.getNgoId());
        return owner;
    }

    @Override
    public Object viewOwner(String userId) {
        Object foundFromRedisOwner = redisTemplate.opsForHash().get(HashKeyForOwner, userId);
        if(foundFromRedisOwner == null){
            log.info("found owner from  db, id is : " + userId);
            return ownerRepo.findByownerId(userId);
        }
        else if(foundFromRedisOwner != null){
            log.info("found owner from redis");
            return foundFromRedisOwner;
        }
        throw new RuntimeException("user not exists");
    }

    @Override
    public String deleteOwner(String userId) {
        if(ownerRepo.existsById(userId)){
            redisTemplate.opsForHash().delete(HashKeyForOwner,userId);
            String ngoId = ownerRepo.findByownerId(userId).getNgoId();
            redisTemplate.opsForGeo().remove(HashKeyForNgoLocation,ngoId);
            ownerRepo.deleteById(userId);
            return "deleted " + userId;
        }
        throw new RuntimeException("user not exists");
    }

    @Override
    public String checkNgoId(String email) {
        return ownerRepo.existsByngoId(email) ? "NGO Already Registered" : "NGO Not Registered" ;
    }

    @Override
    public List<Map<String,Object>> listAllAgentByOwnerId(String id) {
        log.info(id);
        return ownerRepo.findAllAgentByOwnerId(id);
    }

    @Override
    public Resource viewAadharCard(String ngoId) throws MalformedURLException, FileNotFoundException {
        Path filePath = Paths.get(pathToSavedAadharCard).resolve(ngoId);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException(ngoId + " ngo owner has not submitted aadhar card yet");
        }
    }
}
