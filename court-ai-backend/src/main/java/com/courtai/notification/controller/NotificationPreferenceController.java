package com.courtai.notification.controller;

import com.courtai.auth.service.AuthorizationService;
import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;
import com.courtai.notification.service.NotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Endpoints for managing user notification preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @Operation(summary = "Get user notification preferences")
    public ResponseEntity<List<NotificationPreferenceResponse>> getPreferences() {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(preferenceService.getUserPreferences(userUuid));
    }

    @PutMapping
    @Operation(summary = "Update a notification preference")
    public ResponseEntity<NotificationPreferenceResponse> updatePreference(
            @Valid @RequestBody NotificationPreferenceRequest request) {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(preferenceService.updatePreference(userUuid, request));
    }
}