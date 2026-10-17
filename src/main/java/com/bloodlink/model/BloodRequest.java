package com.bloodlink.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "blood_requests")
public class BloodRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String patientName;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BloodGroup bloodGroup;
    @Column(nullable = false) private String hospital;
    @Column(nullable = false) private String city;
    @Column(nullable = false) private int unitsNeeded;
    @Column(nullable = false) private LocalDate neededBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RequestStatus status = RequestStatus.OPEN;
    public Long getId() { return id; } public String getPatientName() { return patientName; } public void setPatientName(String patientName) { this.patientName = patientName; }
    public BloodGroup getBloodGroup() { return bloodGroup; } public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getHospital() { return hospital; } public void setHospital(String hospital) { this.hospital = hospital; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public int getUnitsNeeded() { return unitsNeeded; } public void setUnitsNeeded(int unitsNeeded) { this.unitsNeeded = unitsNeeded; }
    public LocalDate getNeededBy() { return neededBy; } public void setNeededBy(LocalDate neededBy) { this.neededBy = neededBy; }
    public RequestStatus getStatus() { return status; } public void setStatus(RequestStatus status) { this.status = status; }
}
