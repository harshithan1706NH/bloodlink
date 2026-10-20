package com.bloodlink.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final AtomicLong sequence = new AtomicLong();
    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    @GetMapping
    public List<Notification> all() {
        return List.copyOf(notifications);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification send(@Valid @RequestBody SendNotification request) {
        Notification notification = new Notification(
                sequence.incrementAndGet(),
                request.recipient(),
                request.message(),
                Instant.now());
        notifications.add(notification);
        return notification;
    }

    public record SendNotification(
            @NotBlank String recipient,
            @NotBlank String message) {
    }

    public record Notification(
            long id,
            String recipient,
            String message,
            Instant sentAt) {
    }
}
