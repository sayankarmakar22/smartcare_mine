package com.smartcare.SmartCare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {
    private String agentId;
    private String ngoId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private String password;
}
