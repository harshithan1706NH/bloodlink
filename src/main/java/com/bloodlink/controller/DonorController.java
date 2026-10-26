package com.bloodlink.controller;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.Donor;
import com.bloodlink.repository.DonorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/donors")
@CrossOrigin
public class DonorController {

    private final DonorRepository donors;

    public DonorController(DonorRepository donors) {
        this.donors = donors;
    }

    // GET /api/donors
    // GET /api/donors?bloodGroup=O_POSITIVE&city=Chennai
    @GetMapping
    public List<Donor> getAllDonors(
            @RequestParam(required = false) BloodGroup bloodGroup,
            @RequestParam(required = false) String city
    ) {
        if (bloodGroup != null && city != null && !city.isBlank()) {
            return donors.findAvailableDonors(bloodGroup, city.trim());
        }

        return donors.findAll();
    }

    // POST /api/donors
    @PostMapping
    public ResponseEntity<Donor> createDonor(@RequestBody Donor donor) {
        Donor savedDonor = donors.save(donor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedDonor);
    }

    // PATCH /api/donors/1/availability?available=false
    @PatchMapping("/{id}/availability")
    public Donor updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available
    ) {
        Donor donor = donors.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Donor not found"));

        donor.setAvailable(available);

        return donors.save(donor);
    }
}