package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.JudgeReportResponse;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.service.JudgeAnalyticsService;
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
 * REST controller for judge analytics.
 */
@RestController
@RequestMapping("/api/reports/judges")
@RequiredArgsConstructor
@Tag(name = "Reports: Judges", description = "Judge performance and analytics")
public class JudgeReportController {

    private final JudgeAnalyticsService judgeAnalyticsService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get all judge reports", description = "Paginated list of all judges with metrics.")
    public ResponseEntity<ApiResponse<Page<JudgeReportResponse>>> getAllJudgeReports(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Judge reports fetched successfully",
                judgeAnalyticsService.getAllJudgeReports(pageable)));
    }

    @GetMapping("/{judgeUuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get detailed judge report", description = "Deep analytics for a specific judge.")
    public ResponseEntity<ApiResponse<JudgeReportResponse>> getJudgeReport(@PathVariable String judgeUuid) {
        return ResponseEntity.ok(ApiResponse.success(
                "Judge report fetched successfully",
                judgeAnalyticsService.getJudgeReport(judgeUuid)));
    }

    @GetMapping("/{judgeUuid}/disposal-trend")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get judge disposal trend", description = "Monthly cases disposed by the judge.")
    public ResponseEntity<ApiResponse<List<TimeSeriesDataPoint>>> getDisposalTrend(
            @PathVariable String judgeUuid,
            @RequestParam(defaultValue = "2025") int year) {
        return ResponseEntity.ok(ApiResponse.success(
                "Trend fetched successfully",
                judgeAnalyticsService.getDisposalTrend(judgeUuid, year)));
    }

    @GetMapping("/performance-scores")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get judges ranked by performance", description = "Returns judges ordered by composite performance score.")
    public ResponseEntity<ApiResponse<List<JudgeReportResponse>>> getJudgesRankedByPerformance() {
        return ResponseEntity.ok(ApiResponse.success(
                "Rankings fetched successfully",
                judgeAnalyticsService.getJudgesRankedByPerformance()));
    }
}
