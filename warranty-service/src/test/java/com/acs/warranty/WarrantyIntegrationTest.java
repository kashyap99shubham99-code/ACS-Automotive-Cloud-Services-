package com.acs.warranty;

import com.acs.warranty.dto.CreateWarrantyRequest;
import com.acs.warranty.dto.WarrantyResponse;
import com.acs.warranty.event.WarrantyEventPublisher;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.repository.WarrantyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WarrantyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WarrantyRepository warrantyRepository;


    @MockBean
    private WarrantyEventPublisher eventPublisher; // prevent Kafka calls

    @Test
    void createWarranty_integration_shouldPersistAndReturnDto() throws Exception {
        // arrange
        CreateWarrantyRequest req = new CreateWarrantyRequest();
        req.setVehicleVin("VIN-INT-001");
        req.setCustomerId("C-INT-1");
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusDays(30));

        // mock publisher to be no-op
        doNothing().when(eventPublisher).publishWarrantyCreatedEvent(org.mockito.ArgumentMatchers.any(Warranty.class));

        // act & assert
        mockMvc.perform(post("/api/warranties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleVin", is(req.getVehicleVin())))
                .andExpect(jsonPath("$.customerId", is(req.getCustomerId())))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        // repository should have the warranty
        long count = warrantyRepository.countByVehicleVinAndStatus(req.getVehicleVin(), "ACTIVE");
        assert(count == 1);
    }

    @Test
    void getWarrantyByVin_integration_shouldReturnPersisted() throws Exception {
        // prepare entity in repository
        Warranty w = Warranty.createNew("VIN-INT-002", "C-INT-2", LocalDate.now(), LocalDate.now().plusDays(20));
        warrantyRepository.save(w);

        mockMvc.perform(get("/api/warranties/" + w.getVehicleVin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleVin", is(w.getVehicleVin())))
                .andExpect(jsonPath("$.customerId", is(w.getCustomerId())));
    }
}
