package com.tracker.toilet.controller;

import com.tracker.toilet.dto.CreateTicketRequest;
import com.tracker.toilet.dto.TicketResponse;
import com.tracker.toilet.model.TicketStatus;
import com.tracker.toilet.service.MaintenanceTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@CrossOrigin(origins = "*")
public class MaintenanceTicketController {

    private final MaintenanceTicketService ticketService;

    public MaintenanceTicketController(MaintenanceTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "facilityId", required = false) Long facilityId) {
        List<TicketResponse> tickets = ticketService.getAllTickets(search, status, priority, facilityId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable("id") Long id) {
        TicketResponse ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest request) {
        TicketResponse created = ticketService.createTicket(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        TicketStatus ticketStatus = TicketStatus.valueOf(status.trim().toUpperCase());
        TicketResponse updated = ticketService.updateTicketStatus(id, ticketStatus);
        return ResponseEntity.ok(updated);
    }
}
