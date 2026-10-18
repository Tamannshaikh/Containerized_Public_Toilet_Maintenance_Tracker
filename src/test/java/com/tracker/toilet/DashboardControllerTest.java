package com.tracker.toilet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSummaryIndicators() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFacilities", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.operationalFacilities", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.totalTickets", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.averageCleanlinessScore", greaterThan(0.0)));
    }

    @Test
    void shouldReturnAlertsAndExceptions() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/exceptions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAlertsCount", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.overdueTickets").isArray())
                .andExpect(jsonPath("$.waterOutageFacilities").isArray())
                .andExpect(jsonPath("$.lowHygieneFacilities").isArray());
    }
}
