package com.smartcare.SmartCare.Services;

import com.smartcare.SmartCare.DTO.NgoWithKms;

import java.util.List;
import java.util.Map;

public interface RequestServices {
    List<String> findNearestNgoByLongLat(double longitude,double latitude);
    double calculateDistanceBetweenTwoLongLat(double long1,double lat1,double long2,double lat2);

    List<NgoWithKms> sentNearestNgoWithDistance(double longitude, double latitude);

    String bookedNgo(String ngoId,String custId);
}
