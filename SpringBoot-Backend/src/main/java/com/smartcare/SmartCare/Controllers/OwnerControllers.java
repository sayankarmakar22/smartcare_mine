package com.smartcare.SmartCare.Controllers;

import com.smartcare.SmartCare.DTO.CustomerDTO;
import com.smartcare.SmartCare.DTO.CustomerDTOUpdate;
import com.smartcare.SmartCare.DTO.OwnerDTO;
import com.smartcare.SmartCare.Response.MappingResponse;
import com.smartcare.SmartCare.Services.Implementation.CustomerServicesImpl;
import com.smartcare.SmartCare.Services.Implementation.OwnerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/SmartCare/Owner")
public class OwnerControllers {
    @Autowired
    private OwnerServiceImpl ownerService;
    private List<Object> response = new ArrayList<>();

    @PostMapping("/save")
    public ResponseEntity<Object> saveOwner(@RequestBody OwnerDTO ownerDTO){
        try{
            response.clear();
            response.add(ownerService.saveOwner(ownerDTO));
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("okay",response), HttpStatus.CREATED);
        }
        catch (Exception e){
            response.clear();
            response.add("null");
            e.printStackTrace();
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("Error while onboarding new ngo owner",response), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/upload-adhar-Card")
    public ResponseEntity<Object> UploadFile(@RequestParam("file") MultipartFile file,@RequestParam String ngoId){
        try{
            response.clear();
            response.add(ownerService.saveAadharCardToLocalStorage(file,ngoId));
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("okay",response), HttpStatus.CREATED);
        }
        catch (Exception e){
            response.clear();
            response.add("null");
            e.printStackTrace();
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("Error while uploading ngo owner aadhar card",response), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> viewOwner(@PathVariable String id){
        try{
            response.clear();
            response.add(ownerService.viewOwner(id));
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("okay",response), HttpStatus.CREATED);
        }
        catch (Exception e){
            response.clear();
            response.add("No Data Found");
            e.printStackTrace();
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("Error while getting information about ngo-owner",response), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOwner(@PathVariable String id){
        try{
            response.clear();
            response.add(ownerService.deleteOwner(id));
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("okay",response), HttpStatus.CREATED);
        }
        catch (Exception e){
            response.clear();
            response.add("nothing shown here");
            e.printStackTrace();
            return new ResponseEntity<>(MappingResponse.mapUniversalResponse("Error while deleting ngo-owner",response), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/checkNgoId/{ngoId}")
    public ResponseEntity<Object> checkEmail(@PathVariable String ngoId){
        return new ResponseEntity<>(ownerService.checkNgoId(ngoId),HttpStatus.OK);
    }

}
