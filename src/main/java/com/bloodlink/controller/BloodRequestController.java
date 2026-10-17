package com.bloodlink.controller;
import com.bloodlink.model.*;
import com.bloodlink.repository.BloodRequestRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/requests") @CrossOrigin
public class BloodRequestController {
    private final BloodRequestRepository requests;
    public BloodRequestController(BloodRequestRepository requests) { this.requests = requests; }
    @GetMapping public List<BloodRequest> all() { return requests.findAll(); }
    @PostMapping public ResponseEntity<BloodRequest> create(@RequestBody BloodRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(requests.save(request)); }
    @PatchMapping("/{id}/fulfill") public BloodRequest fulfill(@PathVariable Long id) {
        BloodRequest request = requests.findById(id).orElseThrow(() -> new NoSuchElementException("Request not found")); request.setStatus(RequestStatus.FULFILLED); return requests.save(request);
    }
}
