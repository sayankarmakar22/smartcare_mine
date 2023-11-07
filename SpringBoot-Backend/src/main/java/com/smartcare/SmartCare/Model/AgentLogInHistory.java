package com.smartcare.SmartCare.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AgentLogInHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ColumnDefault(value = "0")
    private int device;
    private Date dateTime;
    @ManyToOne
    @JoinColumn(name = "agentId")
    private Agent agent;
}
