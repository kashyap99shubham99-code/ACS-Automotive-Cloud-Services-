package com.acs.warranty.event;
import com.acs.common.event.WarrantyCreatedEvent;
import com.acs.warranty.model.Warranty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WarrantyEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(WarrantyEventPublisher.class);

    private static final String TOPIC = "acs.warranty.created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public WarrantyEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishWarrantyCreatedEvent(Warranty warranty) {

        WarrantyCreatedEvent event = new WarrantyCreatedEvent(
                warranty.getWarrantyId(),
                warranty.getVehicleVin(),
                warranty.getCustomerId(),
                warranty.getStartDate(),
                warranty.getEndDate()
        );

        log.info("Publishing WarrantyCreatedEvent for warrantyId={}",
                warranty.getWarrantyId());

        kafkaTemplate.send(TOPIC, warranty.getWarrantyId(), event);
    }
}
