package com.smartcare.SmartCare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private String agentId;
    private String ngoId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private Date createAt;
}
