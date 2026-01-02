package com.acs.warranty.service;

import com.acs.warranty.dto.WarrantyResponse;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.repository.WarrantyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class WarrantyService {

    private static final Logger log =
            LoggerFactory.getLogger(WarrantyService.class);

    private final WarrantyRepository warrantyRepository;
    private final CacheManager cacheManager;

    // ✅ Constructor Injection
    public WarrantyService(
            WarrantyRepository warrantyRepository,
            CacheManager cacheManager
    ) {
        this.warrantyRepository = warrantyRepository;
        this.cacheManager = cacheManager;
    }

    /**
     * GET ACTIVE WARRANTY BY VIN
     *
     * ✔ Caches DTO (NOT entity)
     * ✔ Explicit Cache HIT / MISS logs
     * ✔ Redis + Spring Cache safe
     *
     * Cache:
     *  - Cache Name : warranties
     *  - Cache Key  : VIN
     */
    @Cacheable(
            value = "warranties",
            key = "#root.args[0]"
    )
    public WarrantyResponse getActiveWarrantyByVin(String vin) {

        // 🔍 Manual cache check ONLY for logging clarity
        Cache cache = cacheManager.getCache("warranties");
        if (cache != null && cache.get(vin) != null) {
            log.info("✅ CACHE HIT → Returning warranty from Redis for VIN={}", vin);
        } else {
            log.info("❌ CACHE MISS → Fetching warranty from DB for VIN={}", vin);
        }

        Warranty warranty = warrantyRepository
                .findByVehicleVinAndStatus(vin, "ACTIVE")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No active warranty found for VIN=" + vin
                        )
                );

        // ✅ Convert ENTITY → DTO before returning
        return WarrantyResponse.from(warranty);
    }
}
