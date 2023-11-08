package com.smartcare.SmartCare.DTO;


import jakarta.annotation.sql.DataSourceDefinitions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDTO {
    private String name;
    private String address;
    private String dob;
    private String email;
    private String phoneNumber;
    private String ngoId;
    private String ngoAddress;
    private String longitude;
    private String latitude;
    private String password;
}
