package com.courtai.advocate.controller;

import com.courtai.advocate.dto.NotificationResponse;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.common.dto.ApiResponse;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for advocate notifications (read + mark-read).
 */
@RestController
@RequestMapping("/advocate/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateNotificationController {

    private final AdvocateSecurityUtil    securityUtil;
    private final NotificationRepository  notificationRepository;

    @GetMapping
    @Operation(summary = "List my notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        var advocate = securityUtil.getCurrentAdvocate();
        String userUuid = advocate.getUser().getUuid();

        var notifPage = notificationRepository.findByRecipientUuidAndIsDeletedFalseOrderByCreatedAtDesc(
                userUuid, PageRequest.of(page, size));

        List<NotificationResponse> responses = notifPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved",
                new PageImpl<>(responses, notifPage.getPageable(), notifPage.getTotalElements())));
    }

    @PatchMapping("/{notificationUuid}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String notificationUuid) {
        var advocate = securityUtil.getCurrentAdvocate();
        String userUuid = advocate.getUser().getUuid();

        Notification notification = notificationRepository
                .findByRecipientUuidAndIsDeletedFalseOrderByCreatedAtDesc(
                        userUuid, PageRequest.of(0, 1000))
                .getContent()
                .stream()
                .filter(n -> notificationUuid.equals(n.getUuid()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "uuid", notificationUuid));

        notification.setIsRead(Boolean.TRUE);
        notificationRepository.save(notification);

        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        var advocate = securityUtil.getCurrentAdvocate();
        String userUuid = advocate.getUser().getUuid();

        List<Notification> unread = notificationRepository
                .findByRecipientUuidAndIsDeletedFalseOrderByCreatedAtDesc(
                        userUuid, PageRequest.of(0, 500, Sort.by("createdAt").descending()))
                .getContent()
                .stream()
                .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                .toList();

        unread.forEach(n -> n.setIsRead(Boolean.TRUE));
        notificationRepository.saveAll(unread);

        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }

    // ── Private Mapper ────────────────────────────────────────────────────

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .uuid(n.getUuid())
                .notificationType(n.getNotificationType())
                .title(n.getTitle())
                .message(n.getMessage())
                .referenceUuid(n.getReferenceUuid())
                .referenceType(n.getReferenceType())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
