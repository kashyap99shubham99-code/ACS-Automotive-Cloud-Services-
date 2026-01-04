package com.acs.warranty.service;

import com.acs.warranty.dto.WarrantyResponse;
import com.acs.warranty.event.WarrantyEventPublisher;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.repository.WarrantyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarrantyServiceTest {

    @Mock
    private WarrantyRepository warrantyRepository;

    @Mock
    private WarrantyEventPublisher eventPublisher;

    @InjectMocks
    private WarrantyService warrantyService;

    @Test
    void getActiveWarrantyByVin_noMatch_throws() {
        String vin = "VIN-123";
        when(warrantyRepository.findAllByVehicleVinAndStatus(vin, "ACTIVE"))
                .thenReturn(Collections.emptyList());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> warrantyService.getActiveWarrantyByVin(vin));
        assertTrue(ex.getMessage().contains("No active warranty"));
    }

    @Test
    void getActiveWarrantyByVin_multipleMatches_throws() {
        String vin = "VIN-123";
        Warranty w1 = Warranty.createNew(vin, "C1", LocalDate.now(), LocalDate.now().plusDays(10));
        Warranty w2 = Warranty.createNew(vin, "C2", LocalDate.now(), LocalDate.now().plusDays(20));
        when(warrantyRepository.findAllByVehicleVinAndStatus(vin, "ACTIVE"))
                .thenReturn(List.of(w1, w2));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> warrantyService.getActiveWarrantyByVin(vin));
        assertTrue(ex.getMessage().contains("Multiple active warranties"));
    }

    @Test
    void getActiveWarrantyByVin_singleMatch_returnsDto() {
        String vin = "VIN-123";
        Warranty w = Warranty.createNew(vin, "C1", LocalDate.now(), LocalDate.now().plusDays(10));
        when(warrantyRepository.findAllByVehicleVinAndStatus(vin, "ACTIVE"))
                .thenReturn(List.of(w));

        WarrantyResponse resp = warrantyService.getActiveWarrantyByVin(vin);
        assertNotNull(resp);
        assertEquals(vin, resp.getVehicleVin());
        assertEquals(w.getCustomerId(), resp.getCustomerId());
    }

    @Test
    void createWarranty_success_publishesEvent_and_returnsDto() {
        Warranty input = Warranty.createNew("VIN-XYZ", "C1", LocalDate.now(), LocalDate.now().plusDays(30));

        when(warrantyRepository.countByVehicleVinAndStatus(input.getVehicleVin(), "ACTIVE")).thenReturn(0L);
        when(warrantyRepository.save(any(Warranty.class))).thenAnswer(inv -> inv.getArgument(0));

        WarrantyResponse resp = warrantyService.createWarranty(input);

        assertNotNull(resp);
        assertEquals(input.getVehicleVin(), resp.getVehicleVin());
        verify(eventPublisher, times(1)).publishWarrantyCreatedEvent(any(Warranty.class));
    }

    @Test
    void createWarranty_duplicate_throws() {
        Warranty input = Warranty.createNew("VIN-XYZ", "C1", LocalDate.now(), LocalDate.now().plusDays(30));

        when(warrantyRepository.countByVehicleVinAndStatus(input.getVehicleVin(), "ACTIVE")).thenReturn(2L);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> warrantyService.createWarranty(input));
        assertTrue(ex.getMessage().contains("An active warranty already exists"));
        verify(eventPublisher, never()).publishWarrantyCreatedEvent(any(Warranty.class));
    }

    @Test
    void createWarranty_eventPublisherThrows_serviceStillReturnsDto() {
        Warranty input = Warranty.createNew("VIN-THROW", "C1", LocalDate.now(), LocalDate.now().plusDays(30));

        when(warrantyRepository.countByVehicleVinAndStatus(input.getVehicleVin(), "ACTIVE")).thenReturn(0L);
        when(warrantyRepository.save(any(Warranty.class))).thenAnswer(inv -> inv.getArgument(0));

        doThrow(new RuntimeException("Kafka down")).when(eventPublisher).publishWarrantyCreatedEvent(any(Warranty.class));

        WarrantyResponse resp = assertDoesNotThrow(() -> warrantyService.createWarranty(input));

        assertNotNull(resp);
        assertEquals(input.getVehicleVin(), resp.getVehicleVin());
        verify(eventPublisher, times(1)).publishWarrantyCreatedEvent(any(Warranty.class));
    }
}
