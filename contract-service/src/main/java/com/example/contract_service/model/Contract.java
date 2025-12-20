package com.example.contract_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Contract {

    @Id
    private String contractId;

    private String warrantyId;
    private String vehicleVin;
    private String customerId;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;
}
