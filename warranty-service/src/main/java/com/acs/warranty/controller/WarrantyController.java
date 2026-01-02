package com.acs.warranty.controller;

import com.acs.warranty.dto.WarrantyResponse;
import com.acs.warranty.service.WarrantyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warranties")
public class WarrantyController {

    private final WarrantyService warrantyService;

    // ✅ Constructor Injection
    public WarrantyController(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    /**
     * GET WARRANTY BY VIN
     *
     * Example:
     * GET /api/warranties/VIN-ACS-61821
     *
     * RULE:
     * - Controller talks ONLY in DTOs
     * - No entity leakage
     */
    @GetMapping("/{vin}")
    public WarrantyResponse getWarrantyByVin(
            @PathVariable("vin") String vin
    ) {
        // ✅ Service already returns DTO
        return warrantyService.getActiveWarrantyByVin(vin);
    }
}
