package com.smartcare.SmartCare.Services.Implementation;

import com.smartcare.SmartCare.DTO.OwnerDTO;
import com.smartcare.SmartCare.Helper.OwnerHelper;
import com.smartcare.SmartCare.Model.Owner;
import com.smartcare.SmartCare.Repository.OwnerRepo;
import com.smartcare.SmartCare.Services.OwnerServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class OwnerServiceImpl implements OwnerServices {
    @Autowired
    private OwnerRepo ownerRepo;
    @Value("${upload.path}")
    private String pathToSavedAadharCard;

    private Logger log = LoggerFactory.getLogger(OwnerServiceImpl.class);
    @Override
    public Boolean saveAadharCardToLocalStorage(MultipartFile file,String ngoId){
        Path filePath = Paths.get(pathToSavedAadharCard, ngoId);
        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            outputStream.write(file.getBytes());
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
    @Override
    public Object saveOwner(OwnerDTO ownerDTO) {
            log.info("saved to owner to db, ngo id is : " + ownerDTO.getNgoId());
            return ownerRepo.save(OwnerHelper.convertIntoOwner(ownerDTO,new Owner()));
    }

    @Override
    public Object viewOwner(String userId) {
        if(ownerRepo.existsById(userId)){
            log.info("found owner from  db, id is : " + userId);
            return ownerRepo.findByownerId(userId);
        }
        throw new RuntimeException("user not exists");
    }

    @Override
    public String deleteOwner(String userId) {
        if(ownerRepo.existsById(userId)){
            ownerRepo.deleteById(userId);
            return "deleted " + userId;
        }
        throw new RuntimeException("user not exists");
    }

    @Override
    public String checkNgoId(String email) {
        return ownerRepo.existsByngoId(email) ? "NGO Already Registered" : "NGO Not Registered" ;
    }
}
