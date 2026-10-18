package com.tracker.toilet.dto;

public class CreateTicketRequest {

    private Long facilityId;
    private String title;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private String reportedBy;

    public CreateTicketRequest() {
    }

    public CreateTicketRequest(Long facilityId, String title, String description, String priority, String reportedBy) {
        this.facilityId = facilityId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.reportedBy = reportedBy;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }
}
