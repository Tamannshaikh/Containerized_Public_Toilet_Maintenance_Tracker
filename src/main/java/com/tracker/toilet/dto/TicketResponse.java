package com.tracker.toilet.dto;

import com.tracker.toilet.model.MaintenanceTicket;
import com.tracker.toilet.model.TicketPriority;
import com.tracker.toilet.model.TicketStatus;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String ticketNumber;
    private Long facilityId;
    private String facilityName;
    private String facilityCode;
    private String ward;
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private String reportedBy;
    private String assignedStaff;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public TicketResponse() {
    }

    public static TicketResponse fromEntity(MaintenanceTicket ticket) {
        TicketResponse dto = new TicketResponse();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        if (ticket.getFacility() != null) {
            dto.setFacilityId(ticket.getFacility().getId());
            dto.setFacilityName(ticket.getFacility().getName());
            dto.setFacilityCode(ticket.getFacility().getFacilityCode());
            dto.setWard(ticket.getFacility().getWard());
        }
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());
        dto.setReportedBy(ticket.getReportedBy());
        dto.setAssignedStaff(ticket.getAssignedStaff());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(String assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
