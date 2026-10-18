package com.tracker.toilet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cleaning_logs")
public class CleaningLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_id", nullable = false)
    private ToiletFacility facility;

    @Column(name = "cleaner_name", nullable = false, length = 100)
    private String cleanerName;

    @Column(name = "cleaned_at", nullable = false)
    private LocalDateTime cleanedAt;

    @Column(name = "water_supplied")
    private boolean waterSupplied = true;

    @Column(name = "soap_available")
    private boolean soapAvailable = true;

    @Column(name = "hygiene_rating")
    private int hygieneRating = 4;

    @Column(length = 255)
    private String remarks;

    public CleaningLog() {
    }

    public CleaningLog(ToiletFacility facility, String cleanerName, boolean waterSupplied, boolean soapAvailable, int hygieneRating, String remarks) {
        this.facility = facility;
        this.cleanerName = cleanerName;
        this.cleanedAt = LocalDateTime.now();
        this.waterSupplied = waterSupplied;
        this.soapAvailable = soapAvailable;
        this.hygieneRating = hygieneRating;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ToiletFacility getFacility() {
        return facility;
    }

    public void setFacility(ToiletFacility facility) {
        this.facility = facility;
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
