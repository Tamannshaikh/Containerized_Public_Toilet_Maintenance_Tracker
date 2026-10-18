package com.tracker.toilet.dto;

import java.util.ArrayList;
import java.util.List;

public class AlertsResponse {

    private List<TicketResponse> overdueTickets = new ArrayList<>();
    private List<FacilityResponse> waterOutageFacilities = new ArrayList<>();
    private List<FacilityResponse> lowHygieneFacilities = new ArrayList<>();
    private int totalAlertsCount;

    public AlertsResponse() {
    }

    public List<TicketResponse> getOverdueTickets() {
        return overdueTickets;
    }

    public void setOverdueTickets(List<TicketResponse> overdueTickets) {
        this.overdueTickets = overdueTickets;
    }

    public List<FacilityResponse> getWaterOutageFacilities() {
        return waterOutageFacilities;
    }

    public void setWaterOutageFacilities(List<FacilityResponse> waterOutageFacilities) {
        this.waterOutageFacilities = waterOutageFacilities;
    }

    public List<FacilityResponse> getLowHygieneFacilities() {
        return lowHygieneFacilities;
    }

    public void setLowHygieneFacilities(List<FacilityResponse> lowHygieneFacilities) {
        this.lowHygieneFacilities = lowHygieneFacilities;
    }

    public int getTotalAlertsCount() {
        return totalAlertsCount;
    }

    public void setTotalAlertsCount(int totalAlertsCount) {
        this.totalAlertsCount = totalAlertsCount;
    }
}
