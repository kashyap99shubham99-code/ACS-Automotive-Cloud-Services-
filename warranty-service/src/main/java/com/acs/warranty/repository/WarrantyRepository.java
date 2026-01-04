package com.acs.warranty.repository;

import com.acs.warranty.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyRepository
        extends JpaRepository<Warranty, String> {

    Optional<Warranty> findByVehicleVinAndStatus(
            String vehicleVin,
            String status
    );

    // NEW: returns all matches (avoids NonUniqueResult issues)
    List<Warranty> findAllByVehicleVinAndStatus(String vehicleVin, String status);

    // NEW: efficient duplicate check
    long countByVehicleVinAndStatus(String vehicleVin, String status);
}
