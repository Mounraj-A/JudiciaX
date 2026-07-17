package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.AuditAnalyticsResponse;
import com.courtai.reports.dto.response.NotificationAnalyticsResponse;
import com.courtai.reports.service.AuditAnalyticsService;
import com.courtai.reports.service.NotificationAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Admin-level operational analytics (Audit, Notification, Storage).
 */
@RestController
@RequestMapping("/api/reports/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Reports: Admin Operations", description = "Audit, Notification, and System Health APIs")
public class AdminReportController {

    private final AuditAnalyticsService auditAnalyticsService;
    private final NotificationAnalyticsService notificationAnalyticsService;

    @GetMapping("/audit")
    @Operation(summary = "Get audit & security analytics", description = "System-wide audit log and security event metrics.")
    public ResponseEntity<ApiResponse<AuditAnalyticsResponse>> getAuditAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(
                "Audit analytics fetched successfully",
                auditAnalyticsService.getAuditReport()));
    }

    @GetMapping("/notification")
    @Operation(summary = "Get notification analytics", description = "Delivery rates and volume trends for notifications.")
    public ResponseEntity<ApiResponse<NotificationAnalyticsResponse>> getNotificationAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(
                "Notification analytics fetched successfully",
                notificationAnalyticsService.getNotificationReport()));
    }
}
