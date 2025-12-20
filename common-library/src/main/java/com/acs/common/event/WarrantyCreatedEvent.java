package com.acs.common.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyCreatedEvent {

    private String warrantyId;
    private String vehicleVin;
    private String customerId;
    private LocalDate startDate;
    private LocalDate endDate;
}
