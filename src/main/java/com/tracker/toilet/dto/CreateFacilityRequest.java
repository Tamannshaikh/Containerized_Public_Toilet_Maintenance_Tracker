package com.tracker.toilet.dto;

public class CreateFacilityRequest {

    private String facilityCode;
    private String name;
    private String ward;
    private String locationAddress;
    private int totalCabins = 4;

    public CreateFacilityRequest() {
    }

    public CreateFacilityRequest(String facilityCode, String name, String ward, String locationAddress, int totalCabins) {
        this.facilityCode = facilityCode;
        this.name = name;
        this.ward = ward;
        this.locationAddress = locationAddress;
        this.totalCabins = totalCabins;
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
}
