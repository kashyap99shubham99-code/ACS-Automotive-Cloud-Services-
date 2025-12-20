package com.example.contract_service.controller;

import com.example.contract_service.model.Contract;
import com.example.contract_service.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private static final Logger log =
            LoggerFactory.getLogger(ContractController.class);

    private final ContractRepository contractRepository;

    public ContractController(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    /**
     * ✅ Get all contracts
     * GET /api/contracts
     */
    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        log.info("Fetching all contracts");
        return ResponseEntity.ok(contractRepository.findAll());
    }

    /**
     * ✅ Get contract by contractId
     * GET /api/contracts/{contractId}
     */
    @GetMapping("/{contractId}")
    public ResponseEntity<Contract> getContractById(
            @PathVariable String contractId) {

        log.info("Fetching contract by contractId={}", contractId);

        return contractRepository.findById(contractId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ✅ Get contract by warrantyId
     * GET /api/contracts/by-warranty/{warrantyId}
     */
    @GetMapping("/by-warranty/{warrantyId}")
    public ResponseEntity<Contract> getContractByWarrantyId(
            @PathVariable String warrantyId) {

        log.info("Fetching contract by warrantyId={}", warrantyId);

        return contractRepository.findByWarrantyId(warrantyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
