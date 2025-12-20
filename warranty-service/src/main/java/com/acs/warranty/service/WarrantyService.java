package com.acs.warranty.service;

import com.acs.warranty.dto.CreateWarrantyRequest;
import com.acs.warranty.event.WarrantyEventPublisher;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.repository.WarrantyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WarrantyService {

    private static final Logger log =
            LoggerFactory.getLogger(WarrantyService.class);

    private final WarrantyRepository warrantyRepository;
    private final WarrantyEventPublisher eventPublisher;

    public WarrantyService(
            WarrantyRepository warrantyRepository,
            WarrantyEventPublisher eventPublisher
    ) {
        this.warrantyRepository = warrantyRepository;
        this.eventPublisher = eventPublisher;
    }

    public Warranty createWarranty(CreateWarrantyRequest request) {

        log.info("Checking existing warranty for VIN={}", request.getVehicleVin());

        // ✅ DUPLICATE CHECK
        warrantyRepository
                .findByVehicleVinAndStatus(request.getVehicleVin(), "ACTIVE")
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Active warranty already exists for vehicle VIN=" +
                                    request.getVehicleVin()
                    );
                });

        // ✅ CREATE WARRANTY
        Warranty warranty = Warranty.createNew(
                request.getVehicleVin(),
                request.getCustomerId(),
                request.getStartDate(),
                request.getEndDate()
        );

        // ✅ SAVE TO DB
        warrantyRepository.save(warranty);

        // ✅ PUBLISH EVENT
        eventPublisher.publishWarrantyCreatedEvent(warranty);

        log.info("Warranty created successfully with id={}", warranty.getWarrantyId());

        return warranty;
    }
}
