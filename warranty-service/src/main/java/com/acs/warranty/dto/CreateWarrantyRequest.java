package com.acs.warranty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateWarrantyRequest {

    @NotBlank(message = "Vehicle VIN must not be blank")
    private String vehicleVin;

    @NotBlank(message = "Customer ID must not be blank")
    private String customerId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be a future date")
    private LocalDate endDate;
}
