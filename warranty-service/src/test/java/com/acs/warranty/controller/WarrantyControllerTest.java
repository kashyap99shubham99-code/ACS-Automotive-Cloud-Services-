package com.acs.warranty.controller;

import com.acs.warranty.dto.CreateWarrantyRequest;
import com.acs.warranty.dto.WarrantyResponse;
import com.acs.warranty.model.Warranty;
import com.acs.warranty.service.WarrantyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

class WarrantyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WarrantyService warrantyService;

    // Ensure Java time module is registered so LocalDate serializes in tests
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        WarrantyController controller = new WarrantyController(warrantyService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createWarranty_validationFails_returns400() throws Exception {
        CreateWarrantyRequest req = new CreateWarrantyRequest();
        req.setVehicleVin(""); // invalid
        req.setCustomerId("");
        String content = mapper.writeValueAsString(req);

        mockMvc.perform(post("/api/warranties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWarrantyByVin_delegatesToService() throws Exception {
        WarrantyResponse resp = new WarrantyResponse();
        // use reflection-like setters via JSON expectations
        when(warrantyService.getActiveWarrantyByVin("VIN-123")).thenReturn(resp);

        mockMvc.perform(get("/api/warranties/VIN-123"))
                .andExpect(status().isOk());
    }

    @Test
    void createWarranty_success_returnsJsonBody() throws Exception {
        CreateWarrantyRequest req = new CreateWarrantyRequest();
        req.setVehicleVin("VIN-ABC");
        req.setCustomerId("C-1");
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusDays(10));

        // Create a domain warranty and convert to DTO so fields are populated
        Warranty domain = Warranty.createNew(req.getVehicleVin(), req.getCustomerId(), req.getStartDate(), req.getEndDate());
        WarrantyResponse resp = WarrantyResponse.from(domain);

        when(warrantyService.createWarranty(any())).thenReturn(resp);

        String content = mapper.writeValueAsString(req);

        mockMvc.perform(post("/api/warranties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleVin", is(domain.getVehicleVin())))
                .andExpect(jsonPath("$.customerId", is(domain.getCustomerId())));
    }
}
