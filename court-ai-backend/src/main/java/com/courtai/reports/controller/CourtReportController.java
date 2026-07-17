package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.CourtReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.service.CourtAnalyticsService;
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
 * REST controller for court analytics.
 */
@RestController
@RequestMapping("/api/reports/courts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Reports: Courts", description = "Court-level performance and analytics")
public class CourtReportController {

    private final CourtAnalyticsService courtAnalyticsService;

    @GetMapping
    @Operation(summary = "Get all court reports", description = "Returns paginated list of all courts with aggregate metrics.")
    public ResponseEntity<ApiResponse<Page<CourtReportResponse>>> getAllCourtReports(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Court reports fetched successfully",
                courtAnalyticsService.getAllCourtReports(pageable)));
    }

    @GetMapping("/{courtId}")
    @Operation(summary = "Get detailed court report", description = "Returns deep analytics for a specific court.")
    public ResponseEntity<ApiResponse<CourtReportResponse>> getCourtReport(@PathVariable Long courtId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Court report fetched successfully",
                courtAnalyticsService.getCourtReport(courtId)));
    }

    @GetMapping("/{courtId}/trend/filing")
    @Operation(summary = "Get monthly filing trend", description = "Monthly cases filed in a specific year.")
    public ResponseEntity<ApiResponse<List<TimeSeriesDataPoint>>> getFilingTrend(
            @PathVariable Long courtId,
            @RequestParam(defaultValue = "2025") int year) {
        return ResponseEntity.ok(ApiResponse.success(
                "Trend fetched successfully",
                courtAnalyticsService.getMonthlyFilingTrend(courtId, year)));
    }

    @GetMapping("/global/status-distribution")
    @Operation(summary = "Get global case status distribution", description = "Aggregate case status counts across all courts.")
    public ResponseEntity<ApiResponse<List<GraphDataPoint>>> getGlobalStatusDistribution() {
        return ResponseEntity.ok(ApiResponse.success(
                "Distribution fetched successfully",
                courtAnalyticsService.getGlobalStatusDistribution()));
    }
}
