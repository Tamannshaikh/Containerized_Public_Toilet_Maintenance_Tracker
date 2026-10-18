package com.tracker.toilet.service;

import com.tracker.toilet.dto.CreateTicketRequest;
import com.tracker.toilet.dto.TicketResponse;
import com.tracker.toilet.exception.BadRequestException;
import com.tracker.toilet.exception.ResourceNotFoundException;
import com.tracker.toilet.model.MaintenanceTicket;
import com.tracker.toilet.model.TicketPriority;
import com.tracker.toilet.model.TicketStatus;
import com.tracker.toilet.model.ToiletFacility;
import com.tracker.toilet.repository.MaintenanceTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaintenanceTicketService {

    private final MaintenanceTicketRepository ticketRepository;
    private final FacilityService facilityService;
    private final AtomicLong sequenceGenerator = new AtomicLong(100);

    public MaintenanceTicketService(MaintenanceTicketRepository ticketRepository, FacilityService facilityService) {
        this.ticketRepository = ticketRepository;
        this.facilityService = facilityService;
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets(String search, String status, String priority, Long facilityId) {
        List<MaintenanceTicket> tickets = ticketRepository.findAllByOrderByCreatedAtDesc();

        return tickets.stream()
                .filter(ticket -> {
                    if (search != null && !search.isBlank()) {
                        String term = search.trim().toLowerCase();
                        boolean matchTitle = ticket.getTitle() != null && ticket.getTitle().toLowerCase().contains(term);
                        boolean matchDesc = ticket.getDescription() != null && ticket.getDescription().toLowerCase().contains(term);
                        boolean matchNum = ticket.getTicketNumber() != null && ticket.getTicketNumber().toLowerCase().contains(term);
                        boolean matchFacility = ticket.getFacility() != null && (
                                ticket.getFacility().getName().toLowerCase().contains(term) ||
                                ticket.getFacility().getFacilityCode().toLowerCase().contains(term) ||
                                ticket.getFacility().getWard().toLowerCase().contains(term)
                        );
                        if (!matchTitle && !matchDesc && !matchNum && !matchFacility) {
                            return false;
                        }
                    }
                    if (status != null && !status.isBlank()) {
                        return ticket.getStatus().name().equalsIgnoreCase(status.trim());
                    }
                    return true;
                })
                .filter(ticket -> {
                    if (priority != null && !priority.isBlank()) {
                        return ticket.getPriority().name().equalsIgnoreCase(priority.trim());
                    }
                    return true;
                })
                .filter(ticket -> {
                    if (facilityId != null) {
                        return ticket.getFacility().getId().equals(facilityId);
                    }
                    return true;
                })
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        MaintenanceTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance ticket not found with ID: " + id));
        return TicketResponse.fromEntity(ticket);
    }

    public TicketResponse createTicket(CreateTicketRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null.");
        }
        if (request.getFacilityId() == null) {
            throw new BadRequestException("Facility ID is required to log a maintenance ticket.");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Ticket issue title is required.");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Ticket description is required.");
        }

        ToiletFacility facility = facilityService.getFacilityEntity(request.getFacilityId());

        TicketPriority priority = TicketPriority.MEDIUM;
        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            try {
                priority = TicketPriority.valueOf(request.getPriority().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid priority value: '" + request.getPriority() + "'. Allowed values: LOW, MEDIUM, HIGH, CRITICAL.");
            }
        }

        String reportedBy = (request.getReportedBy() != null && !request.getReportedBy().isBlank())
                ? request.getReportedBy().trim()
                : "Citizen";

        MaintenanceTicket ticket = new MaintenanceTicket(facility, request.getTitle().trim(), request.getDescription().trim(), priority, reportedBy);
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ticket.setTicketNumber("TKT-" + timestamp + "-" + uniqueSuffix);

        MaintenanceTicket saved = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(saved);
    }

    public TicketResponse updateTicketStatus(Long id, TicketStatus newStatus) {
        if (newStatus == null) {
            throw new BadRequestException("Target ticket status cannot be null.");
        }
        MaintenanceTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance ticket not found with ID: " + id));

        TicketStatus currentStatus = ticket.getStatus();
        if (currentStatus == newStatus) {
            return TicketResponse.fromEntity(ticket);
        }

        // Validate ticket lifecycle state transitions
        if (currentStatus == TicketStatus.CLOSED && newStatus != TicketStatus.OPEN) {
            throw new BadRequestException("Closed ticket '" + ticket.getTicketNumber() + "' cannot be transitioned directly to " + newStatus + ". It must be reopened to OPEN first.");
        }
        if ((currentStatus == TicketStatus.OPEN || currentStatus == TicketStatus.IN_PROGRESS) && newStatus == TicketStatus.CLOSED) {
            throw new BadRequestException("Ticket '" + ticket.getTicketNumber() + "' must be resolved before it can be closed.");
        }

        ticket.setStatus(newStatus);
        MaintenanceTicket updated = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(updated);
    }
}
