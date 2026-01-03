package com.acs.warranty.dto;

import com.acs.warranty.model.Warranty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WarrantyResponse {

    private String warrantyId;
    private String vehicleVin;
    private String customerId;

    // ✅ HARD FIX — independent of ObjectMapper
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    // ✅ HARD FIX
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String status;

    public static WarrantyResponse from(Warranty warranty) {
        WarrantyResponse dto = new WarrantyResponse();
        dto.warrantyId = warranty.getWarrantyId();
        dto.vehicleVin = warranty.getVehicleVin();
        dto.customerId = warranty.getCustomerId();
        dto.startDate = warranty.getStartDate();
        dto.endDate = warranty.getEndDate();
        dto.status = warranty.getStatus();
        return dto;
    }
}
