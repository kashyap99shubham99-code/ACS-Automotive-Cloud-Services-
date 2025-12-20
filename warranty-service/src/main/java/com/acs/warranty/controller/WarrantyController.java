package com.acs.warranty.controller;

import com.acs.warranty.dto.CreateWarrantyRequest;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.service.WarrantyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warranties")
public class WarrantyController {

    private static final Logger log =
            LoggerFactory.getLogger(WarrantyController.class);

    private final WarrantyService warrantyService;

    // ✅ Constructor Injection (BEST PRACTICE)
    public WarrantyController(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Warranty createWarranty(@Valid @RequestBody CreateWarrantyRequest request) {

        log.info("Received create warranty request for VIN={}",
                request.getVehicleVin());

        return warrantyService.createWarranty(request);
    }
}
