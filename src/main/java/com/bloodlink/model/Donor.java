package com.bloodlink.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "donors")
public class Donor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BloodGroup bloodGroup;
    @Column(nullable = false) private String city;
    private LocalDate lastDonationDate;
    private boolean available = true;
    public Long getId() { return id; } public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public BloodGroup getBloodGroup() { return bloodGroup; } public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public LocalDate getLastDonationDate() { return lastDonationDate; } public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public boolean isAvailable() { return available; } public void setAvailable(boolean available) { this.available = available; }
}
