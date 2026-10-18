package com.tracker.toilet.dto;

import com.tracker.toilet.model.ToiletFacility;
import java.time.LocalDateTime;

public class FacilityResponse {

    private Long id;
    private String facilityCode;
    private String name;
    private String ward;
    private String locationAddress;
    private int totalCabins;
    private String operationalStatus;
    private boolean hasRunningWater;
    private double cleanlinessScore;
    private LocalDateTime createdAt;

    public FacilityResponse() {
    }

    public static FacilityResponse fromEntity(ToiletFacility facility) {
        FacilityResponse dto = new FacilityResponse();
        dto.setId(facility.getId());
        dto.setFacilityCode(facility.getFacilityCode());
        dto.setName(facility.getName());
        dto.setWard(facility.getWard());
        dto.setLocationAddress(facility.getLocationAddress());
        dto.setTotalCabins(facility.getTotalCabins());
        dto.setOperationalStatus(facility.getOperationalStatus());
        dto.setHasRunningWater(facility.isHasRunningWater());
        dto.setCleanlinessScore(facility.getCleanlinessScore());
        dto.setCreatedAt(facility.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public int getTotalCabins() {
        return totalCabins;
    }

    public void setTotalCabins(int totalCabins) {
        this.totalCabins = totalCabins;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public boolean isHasRunningWater() {
        return hasRunningWater;
    }

    public void setHasRunningWater(boolean hasRunningWater) {
        this.hasRunningWater = hasRunningWater;
    }

    public double getCleanlinessScore() {
        return cleanlinessScore;
    }

    public void setCleanlinessScore(double cleanlinessScore) {
        this.cleanlinessScore = cleanlinessScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
