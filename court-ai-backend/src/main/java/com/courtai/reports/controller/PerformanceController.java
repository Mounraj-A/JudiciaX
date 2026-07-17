package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.PerformanceReportResponse;
import com.courtai.reports.service.PerformanceAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for System Performance Reports.
 */
@RestController
@RequestMapping("/api/reports/performance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Reports: Performance", description = "Time-boxed system performance metrics")
public class PerformanceController {

    private final PerformanceAnalyticsService performanceAnalyticsService;

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly performance report", description = "Performance metrics for a specific month.")
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.success(
                "Monthly report generated successfully",
                performanceAnalyticsService.getMonthlyReport(year, month)));
    }

    @GetMapping("/quarterly")
    @Operation(summary = "Get quarterly performance report", description = "Performance metrics for a specific quarter.")
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getQuarterlyReport(
            @RequestParam int year,
            @RequestParam int quarter) {
        return ResponseEntity.ok(ApiResponse.success(
                "Quarterly report generated successfully",
                performanceAnalyticsService.getQuarterlyReport(year, quarter)));
    }

    @GetMapping("/annual")
    @Operation(summary = "Get annual performance report", description = "Performance metrics for a full year.")
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getAnnualReport(
            @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success(
                "Annual report generated successfully",
                performanceAnalyticsService.getAnnualReport(year)));
    }
}
