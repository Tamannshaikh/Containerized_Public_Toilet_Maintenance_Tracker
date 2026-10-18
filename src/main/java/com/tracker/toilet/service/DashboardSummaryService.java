package com.tracker.toilet.service;

import com.tracker.toilet.dto.AlertsResponse;
import com.tracker.toilet.dto.FacilityResponse;
import com.tracker.toilet.dto.SummaryIndicatorsResponse;
import com.tracker.toilet.dto.TicketResponse;
import com.tracker.toilet.model.MaintenanceTicket;
import com.tracker.toilet.model.TicketPriority;
import com.tracker.toilet.model.TicketStatus;
import com.tracker.toilet.model.ToiletFacility;
import com.tracker.toilet.repository.MaintenanceTicketRepository;
import com.tracker.toilet.repository.ToiletFacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardSummaryService {

    private final ToiletFacilityRepository facilityRepository;
    private final MaintenanceTicketRepository ticketRepository;

    public DashboardSummaryService(ToiletFacilityRepository facilityRepository,
                                   MaintenanceTicketRepository ticketRepository) {
        this.facilityRepository = facilityRepository;
        this.ticketRepository = ticketRepository;
    }

    public SummaryIndicatorsResponse getSummaryIndicators() {
        List<ToiletFacility> facilities = facilityRepository.findAll();
        List<MaintenanceTicket> tickets = ticketRepository.findAll();

        SummaryIndicatorsResponse summary = new SummaryIndicatorsResponse();
        summary.setTotalFacilities(facilities.size());
        summary.setOperationalFacilities(facilities.stream().filter(f -> "OPERATIONAL".equalsIgnoreCase(f.getOperationalStatus())).count());
        summary.setMaintenanceRequiredFacilities(facilities.stream().filter(f -> !"OPERATIONAL".equalsIgnoreCase(f.getOperationalStatus())).count());

        summary.setTotalTickets(tickets.size());
        summary.setOpenTickets(tickets.stream().filter(t -> t.getStatus() == TicketStatus.OPEN).count());
        summary.setInProgressTickets(tickets.stream().filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS).count());
        summary.setResolvedTickets(tickets.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count());
        summary.setClosedTickets(tickets.stream().filter(t -> t.getStatus() == TicketStatus.CLOSED).count());

        summary.setCriticalTickets(tickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.CRITICAL && t.getStatus() != TicketStatus.RESOLVED && t.getStatus() != TicketStatus.CLOSED)
                .count());

        LocalDateTime now = LocalDateTime.now();
        long overdueCount = tickets.stream()
                .filter(t -> isTicketOverdue(t, now))
                .count();
        summary.setOverdueTickets(overdueCount);

        double avgCleanliness = facilities.isEmpty() ? 0.0 :
                facilities.stream().mapToDouble(ToiletFacility::getCleanlinessScore).average().orElse(0.0);
        summary.setAverageCleanlinessScore(Math.round(avgCleanliness * 10.0) / 10.0);

        return summary;
    }

    public AlertsResponse getAlertsAndExceptions() {
        List<ToiletFacility> facilities = facilityRepository.findAll();
        List<MaintenanceTicket> tickets = ticketRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        List<TicketResponse> overdue = tickets.stream()
                .filter(t -> isTicketOverdue(t, now))
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());

        List<FacilityResponse> waterOutages = facilities.stream()
                .filter(f -> !f.isHasRunningWater())
                .map(FacilityResponse::fromEntity)
                .collect(Collectors.toList());

        List<FacilityResponse> lowHygiene = facilities.stream()
                .filter(f -> f.getCleanlinessScore() < 60.0)
                .map(FacilityResponse::fromEntity)
                .collect(Collectors.toList());

        AlertsResponse alerts = new AlertsResponse();
        alerts.setOverdueTickets(overdue);
        alerts.setWaterOutageFacilities(waterOutages);
        alerts.setLowHygieneFacilities(lowHygiene);
        alerts.setTotalAlertsCount(overdue.size() + waterOutages.size() + lowHygiene.size());

        return alerts;
    }

    private boolean isTicketOverdue(MaintenanceTicket ticket, LocalDateTime now) {
        if (ticket.getStatus() == TicketStatus.RESOLVED || ticket.getStatus() == TicketStatus.CLOSED) {
            return false;
        }
        if (ticket.getCreatedAt() == null) {
            return false;
        }
        long hoursPending = Duration.between(ticket.getCreatedAt(), now).toHours();
        if (ticket.getPriority() == TicketPriority.CRITICAL || ticket.getPriority() == TicketPriority.HIGH) {
            return hoursPending >= 4;
        }
        return hoursPending >= 24;
    }
}
