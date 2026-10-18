package com.tracker.toilet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.toilet.dto.CreateCleaningLogRequest;
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
class CleaningLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCleaningLogSuccessfully() throws Exception {
        CreateCleaningLogRequest request = new CreateCleaningLogRequest(
                1L,
                "Ramesh Patil",
                true,
                true,
                5,
                "Floors scrubbed, soap dispenser restocked."
        );

        mockMvc.perform(post("/api/v1/cleaning-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.facilityId").value(1))
                .andExpect(jsonPath("$.cleanerName").value("Ramesh Patil"))
                .andExpect(jsonPath("$.waterSupplied").value(true))
                .andExpect(jsonPath("$.hygieneRating").value(5));
    }

    @Test
    void shouldReturn400WhenCleanerNameIsMissing() throws Exception {
        CreateCleaningLogRequest request = new CreateCleaningLogRequest(
                1L,
                "", // empty name
                true,
                true,
                4,
                "Shift log"
        );

        mockMvc.perform(post("/api/v1/cleaning-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Cleaner name is required.")));
    }

    @Test
    void shouldReturn400WhenHygieneRatingIsOutOfRange() throws Exception {
        CreateCleaningLogRequest request = new CreateCleaningLogRequest(
                1L,
                "Sunil K.",
                true,
                true,
                6, // invalid > 5
                "Invalid score"
        );

        mockMvc.perform(post("/api/v1/cleaning-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Hygiene rating must be between 1 (poor) and 5 (excellent).")));
    }

    @Test
    void shouldListCleaningLogs() throws Exception {
        mockMvc.perform(get("/api/v1/cleaning-logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
