package com.bloodlink.controller;
import com.bloodlink.model.*;
import com.bloodlink.repository.DonorRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/donors") @CrossOrigin
public class DonorController {
    private final DonorRepository donors;
    public DonorController(DonorRepository donors) { this.donors = donors; }
    @GetMapping public List<Donor> all(@RequestParam(required=false) BloodGroup bloodGroup, @RequestParam(required=false) String city) {
        if (bloodGroup != null && city != null && !city.isBlank()) return donors.findByBloodGroupAndCityIgnoreCaseAndAvailableTrue(bloodGroup, city.trim());
        return donors.findAll();
    }
    @PostMapping public ResponseEntity<Donor> create(@RequestBody Donor donor) { return ResponseEntity.status(HttpStatus.CREATED).body(donors.save(donor)); }
    @PatchMapping("/{id}/availability") public Donor setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        Donor donor = donors.findById(id).orElseThrow(() -> new NoSuchElementException("Donor not found")); donor.setAvailable(available); return donors.save(donor);
    }
}
