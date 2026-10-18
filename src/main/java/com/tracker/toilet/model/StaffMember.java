package com.tracker.toilet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "staff_members")
public class StaffMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_id", nullable = false, unique = true, length = 50)
    private String staffId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 50)
    private String role; // CLEANER, TECHNICIAN, SUPERVISOR

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "assigned_ward", length = 50)
    private String assignedWard;

    @Column(name = "is_active")
    private boolean active = true;

    public StaffMember() {
    }

    public StaffMember(String staffId, String fullName, String role, String contactPhone, String assignedWard) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.role = role;
        this.contactPhone = contactPhone;
        this.assignedWard = assignedWard;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAssignedWard() {
        return assignedWard;
    }

    public void setAssignedWard(String assignedWard) {
        this.assignedWard = assignedWard;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
