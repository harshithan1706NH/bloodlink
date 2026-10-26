package com.bloodlink.notification;

import com.bloodlink.model.BloodRequest;
import com.bloodlink.model.Donor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/** Sends donor alerts to the standalone notification microservice. */
@Service
public class NotificationServiceClient {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);
    private final RestClient client;

    public NotificationServiceClient(
            @Value("${notification.service.url:http://localhost:8081/api}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    public int notifyMatchingDonors(BloodRequest request, List<Donor> matchingDonors) {
        int notified = 0;
        String message = "Blood request: " + request.getBloodGroup() + " needed in "
                + request.getCity() + " at " + request.getHospital()
                + ". Patient: " + request.getPatientName() + ".";

        for (Donor donor : matchingDonors) {
            try {
                client.post()
                        .uri("/notifications")
                        .body(Map.of("recipient", donor.getPhone(), "message", message))
                        .retrieve()
                        .toBodilessEntity();
                notified++;
            } catch (Exception exception) {
                // A notification outage must not prevent the blood request from being created.
                log.warn("Could not notify donor {} through notification service", donor.getId(), exception);
            }
        }
        return notified;
    }
}
