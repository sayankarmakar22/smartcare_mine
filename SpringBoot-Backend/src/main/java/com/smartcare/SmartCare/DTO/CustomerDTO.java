package com.smartcare.SmartCare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String username;
    private String address;
    private String dob;
    private String phoneNumber;
    private String email;
    private String password;
}
