package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.DashboardSummaryResponse;
import com.courtai.reports.service.DashboardAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for dashboard views per role.
 */
@RestController
@RequestMapping("/api/reports/dashboard")
@RequiredArgsConstructor
@Tag(name = "Reports: Dashboard", description = "Dashboard summary APIs for different roles")
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService dashboardAnalyticsService;

    @GetMapping("/court")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get global court dashboard", description = "Returns aggregated stats across all courts. Admin only.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getCourtDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                "Court dashboard generated successfully",
                dashboardAnalyticsService.getCourtDashboard()));
    }

    @GetMapping("/judge")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    @Operation(summary = "Get judge dashboard", description = "Returns stats for the currently authenticated judge.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getJudgeDashboard(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Judge dashboard generated successfully",
                dashboardAnalyticsService.getJudgeDashboard(authentication.getName())));
    }

    @GetMapping("/advocate")
    @PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
    @Operation(summary = "Get advocate dashboard", description = "Returns stats for the currently authenticated advocate.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getAdvocateDashboard(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Advocate dashboard generated successfully",
                dashboardAnalyticsService.getAdvocateDashboard(authentication.getName())));
    }

    @GetMapping("/clerk")
    @PreAuthorize("hasAuthority('ROLE_CLERK')")
    @Operation(summary = "Get clerk dashboard", description = "Returns stats for the currently authenticated clerk.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getClerkDashboard(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Clerk dashboard generated successfully",
                dashboardAnalyticsService.getClerkDashboard(authentication.getName())));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get admin system dashboard", description = "Returns system-wide operational metrics.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                "Admin dashboard generated successfully",
                dashboardAnalyticsService.getAdminDashboard()));
    }
}
