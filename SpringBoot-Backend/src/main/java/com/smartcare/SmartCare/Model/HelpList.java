package com.smartcare.SmartCare.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HelpList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Date requestDate;
    private String longitude;
    private String latitude;
    private String status;
    private Date solvedTime;
    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    private String ngoId;
}
