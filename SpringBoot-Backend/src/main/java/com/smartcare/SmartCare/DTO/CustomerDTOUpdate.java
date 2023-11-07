package com.smartcare.SmartCare.DTO;

import lombok.Data;

@Data
public class CustomerDTOUpdate {
    private String userId;
    private String username;
    private String address;
    private String dob;
    private String email;
    private String phoneNumber;
}
