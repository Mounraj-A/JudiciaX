package com.courtai.notification.controller;

import com.courtai.notification.service.NotificationEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/notification-events")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Admin endpoints for managing notification events")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class NotificationEventController {

    private final NotificationEventService eventService;

    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed notification events")
    public ResponseEntity<Void> retryFailedEvents() {
        eventService.retryFailedEvents();
        return ResponseEntity.ok().build();
    }
}