package com.acs.warranty.controller;

import com.acs.warranty.dto.CreateWarrantyRequest;
import com.acs.warranty.dto.WarrantyResponse;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.service.WarrantyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * CREATE WARRANTY
     *
     * Example:
     * POST /api/warranties
     * Body: CreateWarrantyRequest (vehicleVin, customerId, startDate, endDate)
     *
     * Flow:
     * 1. Request body is validated (@Valid).
     * 2. Controller maps DTO -> domain entity using the domain factory `Warranty.createNew(...)`.
     * 3. Controller calls `WarrantyService.createWarranty(...)` which persists the entity and
     *    evicts the cache entry for the VIN so reads are fresh.
     * 4. Service returns a DTO (WarrantyResponse) which the controller returns to the client.
     */
    @PostMapping
    public WarrantyResponse createWarranty(
            @Valid @RequestBody CreateWarrantyRequest request
    ) {
        // Map DTO -> Entity using domain factory (keeps mapping logic centralized)
        Warranty warranty = Warranty.createNew(
                request.getVehicleVin(),
                request.getCustomerId(),
                request.getStartDate(),
                request.getEndDate()
        );

        // Delegate to service which handles persistence and cache eviction
        return warrantyService.createWarranty(warranty);
    }
}
