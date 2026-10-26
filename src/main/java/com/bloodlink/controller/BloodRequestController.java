package com.bloodlink.controller;
import com.bloodlink.model.*;
import com.bloodlink.notification.NotificationServiceClient;
import com.bloodlink.repository.DonorRepository;
import com.bloodlink.repository.BloodRequestRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/requests") @CrossOrigin
public class BloodRequestController {
    private final BloodRequestRepository requests;
    private final DonorRepository donors;
    private final NotificationServiceClient notifications;

    public BloodRequestController(BloodRequestRepository requests, DonorRepository donors,
                                  NotificationServiceClient notifications) {
        this.requests = requests;
        this.donors = donors;
        this.notifications = notifications;
    }
    @GetMapping public List<BloodRequest> all(@RequestParam(required=false) String city) {
        if (city != null && !city.isBlank()) return requests.findByStatusAndCity(RequestStatus.OPEN, city.trim());
        return requests.findAll();
    }
    @PostMapping
    public ResponseEntity<BloodRequest> create(@RequestBody BloodRequest request) {
        BloodRequest saved = requests.save(request);
        var matchingDonors = donors.findAvailableDonors(saved.getBloodGroup(), saved.getCity());
        int notified = notifications.notifyMatchingDonors(saved, matchingDonors);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-Donors-Notified", String.valueOf(notified))
                .body(saved);
    }
    @PatchMapping("/{id}/fulfill") public BloodRequest fulfill(@PathVariable Long id) {
        BloodRequest request = requests.findById(id).orElseThrow(() -> new NoSuchElementException("Request not found")); request.setStatus(RequestStatus.FULFILLED); return requests.save(request);
    }
}
