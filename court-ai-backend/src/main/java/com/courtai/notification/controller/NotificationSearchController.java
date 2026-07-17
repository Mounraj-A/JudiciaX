package com.courtai.notification.controller;

import com.courtai.auth.service.AuthorizationService;
import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSearchRequest;
import com.courtai.notification.service.NotificationSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/search")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Advanced search for notifications")
public class NotificationSearchController {

    private final NotificationSearchService searchService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "Search notifications with advanced filters")
    public ResponseEntity<Page<NotificationResponse>> searchNotifications(
            @RequestBody NotificationSearchRequest request) {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(searchService.search(userUuid, request));
    }
}