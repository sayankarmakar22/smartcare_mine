package com.smartcare.SmartCare.Services;

import com.smartcare.SmartCare.DTO.CustomerDTO;
import com.smartcare.SmartCare.DTO.CustomerDTOUpdate;
import com.smartcare.SmartCare.DTO.OwnerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OwnerServices {
    Object saveOwner(OwnerDTO ownerDTO);
    Boolean saveAadharCardToLocalStorage(MultipartFile file,String ngoId) throws IOException;
    Object viewOwner(String userId);
    String deleteOwner(String userId);
    String checkNgoId(String email);
}
