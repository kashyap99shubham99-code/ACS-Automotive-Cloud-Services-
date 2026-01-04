package com.acs.warranty.service;

import com.acs.warranty.dto.WarrantyResponse;
import com.acs.warranty.event.WarrantyEventPublisher;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.repository.WarrantyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WarrantyService {

    private static final Logger log =
            LoggerFactory.getLogger(WarrantyService.class);

    private final WarrantyRepository warrantyRepository;
    private final WarrantyEventPublisher eventPublisher;

    // ✅ Constructor Injection
    public WarrantyService(
            WarrantyRepository warrantyRepository,
            WarrantyEventPublisher eventPublisher
    ) {
        this.warrantyRepository = warrantyRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * GET ACTIVE WARRANTY BY VIN
     */
    @Cacheable(
            value = "warranties",
            key = "#root.args[0]"
    )
    public WarrantyResponse getActiveWarrantyByVin(String vin) {

        log.debug("Fetching active warranty for VIN={}", vin);

        // Use findAll to tolerate multiple results and provide clearer error message
        List<Warranty> matches = warrantyRepository.findAllByVehicleVinAndStatus(vin, "ACTIVE");

        if (matches.isEmpty()) {
            throw new IllegalStateException("No active warranty found");
        }
        if (matches.size() > 1) {
            throw new IllegalStateException("Multiple active warranties found for VIN=" + vin + "; data inconsistent (count=" + matches.size() + ")");
        }

        return WarrantyResponse.from(matches.get(0));
    }

    /**
     * CREATE WARRANTY (transactional + cache update + event publish)
     *
     * Use @CachePut with the method result as the cache key to avoid evaluating
     * properties on a potentially null incoming argument. Also add a condition
     * so the cache is only updated when the result is non-null.
     *
     * ADDED: validation to prevent creating duplicate warranties for the same VIN
     * with ACTIVE status.
     */
    @Transactional
    @CachePut(value = "warranties", key = "#result.vehicleVin", condition = "#result != null")
    public WarrantyResponse createWarranty(Warranty warranty) {
        // --- VALIDATION / DUPLICATE CHECK (ADDED) ---
        if (warranty == null) {
            throw new IllegalArgumentException("Warranty must not be null");
        }
        if (warranty.getVehicleVin() == null || warranty.getVehicleVin().trim().isEmpty()) {
            throw new IllegalArgumentException("vehicleVin must be provided");
        }

        // Prevent creating a duplicate ACTIVE warranty for the same VIN (use count to avoid NonUniqueResult)
        long duplicateCount;
        try {
            duplicateCount = warrantyRepository.countByVehicleVinAndStatus(warranty.getVehicleVin(), "ACTIVE");
        } catch (jakarta.persistence.NonUniqueResultException | org.hibernate.NonUniqueResultException e) {
            // Translate to IllegalStateException so controller advice maps it to 409
            throw new IllegalStateException("Multiple active warranties already exist for VIN=" + warranty.getVehicleVin());
        }

        if (duplicateCount > 0) {
            throw new IllegalStateException("An active warranty already exists for VIN=" + warranty.getVehicleVin());
        }
        // --- end validation ---

        // 1) Set server-side fields
        if (warranty.getWarrantyId() == null || warranty.getWarrantyId().isEmpty()) {
            warranty.setWarrantyId(UUID.randomUUID().toString());
        }
        if (warranty.getStatus() == null || warranty.getStatus().isEmpty()) {
            warranty.setStatus("ACTIVE");
        }

        // 2) Persist entity
        Warranty saved = warrantyRepository.save(warranty);

        // 3) Publish domain event (non-blocking send)
        try {
            eventPublisher.publishWarrantyCreatedEvent(saved);
        } catch (Exception e) {
            log.warn("Failed to publish WarrantyCreatedEvent for warrantyId={}: {}", saved.getWarrantyId(), e.getMessage());
        }

        // 4) Return DTO
        return WarrantyResponse.from(saved);
    }

}
