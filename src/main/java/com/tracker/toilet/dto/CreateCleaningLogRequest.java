package com.tracker.toilet.dto;

public class CreateCleaningLogRequest {

    private Long facilityId;
    private String cleanerName;
    private boolean waterSupplied = true;
    private boolean soapAvailable = true;
    private int hygieneRating = 4; // 1 to 5
    private String remarks;

    public CreateCleaningLogRequest() {
    }

    public CreateCleaningLogRequest(Long facilityId, String cleanerName, boolean waterSupplied, boolean soapAvailable, int hygieneRating, String remarks) {
        this.facilityId = facilityId;
        this.cleanerName = cleanerName;
        this.waterSupplied = waterSupplied;
        this.soapAvailable = soapAvailable;
        this.hygieneRating = hygieneRating;
        this.remarks = remarks;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getCleanerName() {
        return cleanerName;
    }

    public void setCleanerName(String cleanerName) {
        this.cleanerName = cleanerName;
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
