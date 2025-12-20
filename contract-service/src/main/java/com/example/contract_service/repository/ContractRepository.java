package com.example.contract_service.repository;

import com.example.contract_service.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, String> {

    Optional<Contract> findByWarrantyId(String warrantyId);
}
