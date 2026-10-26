package com.bloodlink.controller;

import com.bloodlink.model.RequestStatus;
import com.bloodlink.repository.BloodRequestRepository;
import com.bloodlink.repository.DonorRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Provides aggregate figures for the desktop dashboard or an admin view. */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {
    private final DonorRepository donors;
    private final BloodRequestRepository requests;

    public DashboardController(DonorRepository donors, BloodRequestRepository requests) {
        this.donors = donors;
        this.requests = requests;
    }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return new DashboardSummary(
                donors.count(),
                donors.countByAvailableTrue(),
                requests.count(),
                requests.countByStatus(RequestStatus.OPEN),
                requests.countByStatus(RequestStatus.FULFILLED));
    }

    public record DashboardSummary(
            long totalDonors,
            long availableDonors,
            long totalRequests,
            long openRequests,
            long fulfilledRequests) {
    }
}
