package com.bloodlink.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "donors")
public class Donor extends Person {
    @Column(nullable = false, unique = true) private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BloodGroup bloodGroup;
    @Column(nullable = false) private String city;
    private LocalDate lastDonationDate;
    private boolean available = true;
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public BloodGroup getBloodGroup() { return bloodGroup; } public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public LocalDate getLastDonationDate() { return lastDonationDate; } public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public boolean isAvailable() { return available; } public void setAvailable(boolean available) { this.available = available; }
}
