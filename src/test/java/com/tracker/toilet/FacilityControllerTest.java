package com.tracker.toilet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.toilet.dto.CreateFacilityRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListAllFacilities() throws Exception {
        mockMvc.perform(get("/api/v1/facilities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].facilityCode").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].ward").exists());
    }

    @Test
    void shouldGetFacilityById() throws Exception {
        mockMvc.perform(get("/api/v1/facilities/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.facilityCode").value("FAC-MUM-001"));
    }

    @Test
    void shouldReturn404WhenFacilityNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/facilities/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("Toilet facility not found with ID: 9999")));
    }

    @Test
    void shouldCreateNewFacilitySuccessfully() throws Exception {
        CreateFacilityRequest request = new CreateFacilityRequest(
                "FAC-TEST-099",
                "Kurla West Terminus Convenience",
                "L",
                "Station Road, Kurla West",
                5
        );

        mockMvc.perform(post("/api/v1/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.facilityCode").value("FAC-TEST-099"))
                .andExpect(jsonPath("$.name").value("Kurla West Terminus Convenience"))
                .andExpect(jsonPath("$.ward").value("L"))
                .andExpect(jsonPath("$.totalCabins").value(5));
    }

    @Test
    void shouldFilterFacilitiesByWard() throws Exception {
        mockMvc.perform(get("/api/v1/facilities")
                        .param("ward", "G/North")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].ward").value("G/North"));
    }
}
