package com.tracker.toilet.dto;

import com.tracker.toilet.model.CleaningLog;
import java.time.LocalDateTime;

public class CleaningLogResponse {

    private Long id;
    private Long facilityId;
    private String facilityName;
    private String facilityCode;
    private String ward;
    private String cleanerName;
    private LocalDateTime cleanedAt;
    private boolean waterSupplied;
    private boolean soapAvailable;
    private int hygieneRating;
    private String remarks;

    public CleaningLogResponse() {
    }

    public static CleaningLogResponse fromEntity(CleaningLog log) {
        CleaningLogResponse dto = new CleaningLogResponse();
        dto.setId(log.getId());
        if (log.getFacility() != null) {
            dto.setFacilityId(log.getFacility().getId());
            dto.setFacilityName(log.getFacility().getName());
            dto.setFacilityCode(log.getFacility().getFacilityCode());
            dto.setWard(log.getFacility().getWard());
        }
        dto.setCleanerName(log.getCleanerName());
        dto.setCleanedAt(log.getCleanedAt());
        dto.setWaterSupplied(log.isWaterSupplied());
        dto.setSoapAvailable(log.isSoapAvailable());
        dto.setHygieneRating(log.getHygieneRating());
        dto.setRemarks(log.getRemarks());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCleanerName() {
        return cleanerName;
    }

    public void setCleanerName(String cleanerName) {
        this.cleanerName = cleanerName;
    }

    public LocalDateTime getCleanedAt() {
        return cleanedAt;
    }

    public void setCleanedAt(LocalDateTime cleanedAt) {
        this.cleanedAt = cleanedAt;
    }

    public boolean isWaterSupplied() {
        return waterSupplied;
    }

    public void setWaterSupplied(boolean waterSupplied) {
        this.waterSupplied = waterSupplied;
    }

    public boolean isSoapAvailable() {
        return soapAvailable;
    }

    public void setSoapAvailable(boolean soapAvailable) {
        this.soapAvailable = soapAvailable;
    }

    public int getHygieneRating() {
        return hygieneRating;
    }

    public void setHygieneRating(int hygieneRating) {
        this.hygieneRating = hygieneRating;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
