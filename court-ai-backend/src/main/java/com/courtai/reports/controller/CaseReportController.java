package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.*;
import com.courtai.reports.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for case-level analytics (including backlog, delay, priority, and trust).
 */
@RestController
@RequestMapping("/api/reports/cases")
@RequiredArgsConstructor
@Tag(name = "Reports: Cases", description = "Case analytics, backlog, delay, and AI trust/priority scores")
public class CaseReportController {

    private final CaseAnalyticsService caseAnalyticsService;
    private final BacklogAnalyticsService backlogAnalyticsService;
    private final DelayAnalyticsService delayAnalyticsService;
    private final PriorityAnalyticsService priorityAnalyticsService;
    private final TrustAnalyticsService trustAnalyticsService;

    // ── General Case Reports ─────────────────────────────────────────────

    @GetMapping("/{caseUuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_JUDGE')")
    @Operation(summary = "Get case lifecycle report", description = "Full lifecycle and AI metrics for a single case.")
    public ResponseEntity<ApiResponse<CaseReportResponse>> getCaseReport(@PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success(
                "Case report fetched successfully",
                caseAnalyticsService.getCaseReport(caseUuid)));
    }

    @GetMapping("/status-distribution")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_JUDGE')")
    @Operation(summary = "Get case status distribution", description = "Global case count grouped by status.")
    public ResponseEntity<ApiResponse<List<GraphDataPoint>>> getStatusDistribution() {
        return ResponseEntity.ok(ApiResponse.success(
                "Distribution fetched successfully",
                caseAnalyticsService.getStatusDistribution()));
    }

    // ── Backlog & Delay ──────────────────────────────────────────────────

    @GetMapping("/delay")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get delay analytics", description = "Delay statistics and age distributions.")
    public ResponseEntity<ApiResponse<DelayAnalyticsResponse>> getDelayAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(
                "Delay analytics fetched successfully",
                delayAnalyticsService.getDelayReport()));
    }

    @GetMapping("/backlog")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get backlog analytics", description = "Cases pending beyond expected timelines.")
    public ResponseEntity<ApiResponse<DelayAnalyticsResponse>> getBacklogAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(
                "Backlog analytics fetched successfully",
                backlogAnalyticsService.getBacklogReport()));
    }

    // ── AI Explainability (Priority & Trust) ─────────────────────────────

    @GetMapping("/priority")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_JUDGE')")
    @Operation(summary = "Get priority analytics", description = "JPI (Judicial Priority Index) distribution and metrics.")
    public ResponseEntity<ApiResponse<PriorityAnalyticsResponse>> getPriorityAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(
                "Priority analytics fetched successfully",
                priorityAnalyticsService.getPriorityReport()));
    }

    @GetMapping("/trust")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_JUDGE')")
    @Operation(summary = "Get trust analytics", description = "CTS (Case Trust Score) distribution and verification impacts.")
    public ResponseEntity<ApiResponse<TrustAnalyticsResponse>> getTrustAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(
                "Trust analytics fetched successfully",
                trustAnalyticsService.getTrustReport()));
    }
}
