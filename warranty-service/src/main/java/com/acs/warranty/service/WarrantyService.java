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
     * ✔ Redis cache enabled
     * ✔ DTO cached (not JPA entity)
     * ✔ No user-controlled data logged (Sonar-safe)
     * ✔ Works with Spring CacheInterceptor
     */
    @Cacheable(
            value = "warranties",
            key = "#root.args[0]"
    )
    public WarrantyResponse getActiveWarrantyByVin(String vin) {

        // 🔍 Cache check ONLY for observability (no user data logged)
        Cache cache = cacheManager.getCache("warranties");
        if (cache != null && cache.get(vin) != null) {
            log.info("✅ CACHE HIT → Returning warranty from Redis");
        } else {
            log.info("❌ CACHE MISS → Fetching warranty from DB");
        }

        Warranty warranty = warrantyRepository
                .findByVehicleVinAndStatus(vin, "ACTIVE")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No active warranty found"
                        )
                );

        // ✅ Convert ENTITY → DTO
        return WarrantyResponse.from(warranty);
    }
}
