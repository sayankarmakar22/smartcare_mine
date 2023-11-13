package com.smartcare.SmartCare.Controllers;

import com.smartcare.SmartCare.DTO.NgoWithKms;
import com.smartcare.SmartCare.Services.Implementation.RequestServicesImpl;
import com.smartcare.SmartCare.Services.RequestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SmartCare/request")
public class RequestControllers {
    @Autowired
    private RequestServicesImpl requestServices;
    @GetMapping("/nearestNgo/{lon}/{lat}")
    public ResponseEntity<List<NgoWithKms>> getNgo(@PathVariable double lon, @PathVariable double lat){
        return new ResponseEntity<>(requestServices.sentNearestNgoWithDistance(lon,lat), HttpStatus.FOUND);
    }

    @PostMapping("/book-ngo")
    public ResponseEntity<String> bookNgo(@PathVariable String ngoId,@PathVariable String custId){
        return new ResponseEntity<>(requestServices.bookedNgo(ngoId,custId),HttpStatus.ACCEPTED);
    }
}
