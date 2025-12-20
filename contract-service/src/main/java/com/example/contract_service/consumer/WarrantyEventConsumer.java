package com.example.contract_service.consumer;

import com.acs.common.event.WarrantyCreatedEvent;
import com.example.contract_service.model.Contract;
import com.example.contract_service.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WarrantyEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(WarrantyEventConsumer.class);

    private final ContractRepository contractRepository;

    public WarrantyEventConsumer(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @KafkaListener(
            topics = "acs.warranty.created",
            groupId = "contract-service-group"
    )
    public void consumeWarrantyCreatedEvent(WarrantyCreatedEvent event) {

        // 🔐 IDEMPOTENCY CHECK
        if (contractRepository.findByWarrantyId(event.getWarrantyId()).isPresent()) {
            log.warn("Contract already exists for warrantyId={}, skipping",
                    event.getWarrantyId());
            return;
        }

        Contract contract = new Contract();
        contract.setContractId(UUID.randomUUID().toString());
        contract.setWarrantyId(event.getWarrantyId());
        contract.setVehicleVin(event.getVehicleVin());
        contract.setCustomerId(event.getCustomerId());
        contract.setStartDate(event.getStartDate());
        contract.setEndDate(event.getEndDate());
        contract.setStatus("ACTIVE");

        contractRepository.save(contract);

        log.info("Contract created successfully for warrantyId={}",
                event.getWarrantyId());
    }
}
