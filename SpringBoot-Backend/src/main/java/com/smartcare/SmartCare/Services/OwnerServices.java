package com.smartcare.SmartCare.Services;

import com.smartcare.SmartCare.DTO.OwnerDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public interface OwnerServices {
    Object saveOwner(OwnerDTO ownerDTO);
    Boolean saveAadharCardToLocalStorage(MultipartFile file,String ngoId) throws IOException;
    Object viewOwner(String userId);
    String deleteOwner(String userId);
    String checkNgoId(String email);

    Resource viewAadharCard(String ngoId) throws MalformedURLException, FileNotFoundException;
}
