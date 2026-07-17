package com.courtai.notification.controller;

import com.courtai.auth.service.AuthorizationService;
import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.UnreadCountResponse;
import com.courtai.notification.service.NotificationHistoryService;
import com.courtai.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Module", description = "Endpoints for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationHistoryService notificationHistoryService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @Operation(summary = "Get user notifications")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userUuid = authorizationService.getCurrentUserUuid();
        return ResponseEntity.ok(notificationHistoryService.getNotificationHistory(userUuid, page, size));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount() {
        String userUuid = authorizationService.getCurrentUserUuid();
        long count = notificationService.getUnreadCount(userUuid);
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead() {
        String userUuid = authorizationService.getCurrentUserUuid();
        notificationService.markAllAsRead(userUuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable String uuid) {
        notificationService.deleteNotification(uuid);
        return ResponseEntity.noContent().build();
    }
}