package com.acs.warranty.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warranty {

    @Id
    private String warrantyId;

    private String vehicleVin;
    private String customerId;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;

    public static Warranty createNew(
            String vehicleVin,
            String customerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return Warranty.builder()
                .warrantyId(UUID.randomUUID().toString())
                .vehicleVin(vehicleVin)
                .customerId(customerId)
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .build();
    }
}
