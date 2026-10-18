package com.tracker.toilet.dto;

public class SummaryIndicatorsResponse {

    private long totalFacilities;
    private long operationalFacilities;
    private long maintenanceRequiredFacilities;
    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long criticalTickets;
    private long overdueTickets;
    private double averageCleanlinessScore;

    public SummaryIndicatorsResponse() {
    }

    public long getTotalFacilities() {
        return totalFacilities;
    }

    public void setTotalFacilities(long totalFacilities) {
        this.totalFacilities = totalFacilities;
    }

    public long getOperationalFacilities() {
        return operationalFacilities;
    }

    public void setOperationalFacilities(long operationalFacilities) {
        this.operationalFacilities = operationalFacilities;
    }

    public long getMaintenanceRequiredFacilities() {
        return maintenanceRequiredFacilities;
    }

    public void setMaintenanceRequiredFacilities(long maintenanceRequiredFacilities) {
        this.maintenanceRequiredFacilities = maintenanceRequiredFacilities;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }

    public long getInProgressTickets() {
        return inProgressTickets;
    }

    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }

    public long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public long getClosedTickets() {
        return closedTickets;
    }

    public void setClosedTickets(long closedTickets) {
        this.closedTickets = closedTickets;
    }

    public long getCriticalTickets() {
        return criticalTickets;
    }

    public void setCriticalTickets(long criticalTickets) {
        this.criticalTickets = criticalTickets;
    }

    public long getOverdueTickets() {
        return overdueTickets;
    }

    public void setOverdueTickets(long overdueTickets) {
        this.overdueTickets = overdueTickets;
    }

    public double getAverageCleanlinessScore() {
        return averageCleanlinessScore;
    }

    public void setAverageCleanlinessScore(double averageCleanlinessScore) {
        this.averageCleanlinessScore = averageCleanlinessScore;
    }
}
