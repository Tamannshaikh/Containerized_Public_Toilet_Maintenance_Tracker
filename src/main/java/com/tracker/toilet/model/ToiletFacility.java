package com.tracker.toilet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "toilet_facilities")
public class ToiletFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_code", nullable = false, unique = true, length = 50)
    private String facilityCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 50)
    private String ward;

    @Column(name = "location_address", length = 255)
    private String locationAddress;

    @Column(name = "total_cabins")
    private int totalCabins;

    @Column(name = "operational_status", length = 30)
    private String operationalStatus = "OPERATIONAL";

    @Column(name = "has_running_water")
    private boolean hasRunningWater = true;

    @Column(name = "cleanliness_score")
    private double cleanlinessScore = 85.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ToiletFacility() {
    }

    public ToiletFacility(String facilityCode, String name, String ward, String locationAddress, int totalCabins) {
        this.facilityCode = facilityCode;
        this.name = name;
        this.ward = ward;
        this.locationAddress = locationAddress;
        this.totalCabins = totalCabins;
        this.operationalStatus = "OPERATIONAL";
        this.hasRunningWater = true;
        this.cleanlinessScore = 85.0;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

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
