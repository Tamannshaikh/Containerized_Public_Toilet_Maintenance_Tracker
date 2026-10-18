package com.tracker.toilet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.toilet.dto.CreateTicketRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MaintenanceTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListAllMaintenanceTickets() throws Exception {
        mockMvc.perform(get("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].ticketNumber").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].status").exists());
    }

    @Test
    void shouldGetTicketById() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketNumber").value("TKT-20261018-001"))
                .andExpect(jsonPath("$.facilityName").value("Dadar Station West Public Convenience"));
    }

    @Test
    void shouldCreateMaintenanceTicketSuccessfully() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest(
                1L,
                "Broken Door Latch in Cabin 1",
                "Lock mechanism damaged, privacy issue for citizens.",
                "HIGH",
                "Inspector Kulkarni"
        );

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.ticketNumber").exists())
                .andExpect(jsonPath("$.title").value("Broken Door Latch in Cabin 1"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.facilityId").value(1))
                .andExpect(jsonPath("$.reportedBy").value("Inspector Kulkarni"));
    }

    @Test
    void shouldReturn400WhenTitleIsMissing() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest(
                1L,
                "", // empty title
                "Missing tap nozzle",
                "LOW",
                "Citizen"
        );

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Ticket issue title is required."));
    }

    @Test
    void shouldReturn404WhenFacilityNotFoundOnTicketCreation() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest(
                9999L, // non-existent facility
                "Water pump trip",
                "Motor tripped due to low voltage",
                "CRITICAL",
                "Operator"
        );

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("Toilet facility not found with ID: 9999")));
    }

    @Test
    void shouldReturn404WhenTicketIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("Maintenance ticket not found with ID: 9999")));
    }

    @Test
    void shouldUpdateTicketStatusSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/v1/tickets/1/status")
                        .param("status", "RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.resolvedAt").exists());
    }

    @Test
    void shouldRejectInvalidDirectStatusTransitionFromOpenToClosed() throws Exception {
        // Ticket 2 is IN_PROGRESS/OPEN and cannot jump directly to CLOSED without being RESOLVED
        mockMvc.perform(patch("/api/v1/tickets/2/status")
                        .param("status", "CLOSED"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("must be resolved before it can be closed")));
    }

    @Test
    void shouldFilterTicketsBySearchKeyword() throws Exception {
        mockMvc.perform(get("/api/v1/tickets")
                        .param("search", "flush")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", containsStringIgnoringCase("flush")));
    }
}
