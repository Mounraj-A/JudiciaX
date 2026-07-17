package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.AdvocateReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.service.AdvocateAnalyticsService;
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
 * REST controller for advocate analytics.
 */
@RestController
@RequestMapping("/api/reports/advocates")
@RequiredArgsConstructor
@Tag(name = "Reports: Advocates", description = "Advocate performance and analytics")
public class AdvocateReportController {

    private final AdvocateAnalyticsService advocateAnalyticsService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get all advocate reports", description = "Paginated list of advocates with filing metrics.")
    public ResponseEntity<ApiResponse<Page<AdvocateReportResponse>>> getAllAdvocateReports(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Advocate reports fetched successfully",
                advocateAnalyticsService.getAllAdvocateReports(pageable)));
    }

    @GetMapping("/{advocateUuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get detailed advocate report", description = "Analytics for a specific advocate.")
    public ResponseEntity<ApiResponse<AdvocateReportResponse>> getAdvocateReport(@PathVariable String advocateUuid) {
        return ResponseEntity.ok(ApiResponse.success(
                "Advocate report fetched successfully",
                advocateAnalyticsService.getAdvocateReport(advocateUuid)));
    }

    @GetMapping("/{advocateUuid}/filing-trend")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get advocate filing trend", description = "Monthly cases filed by the advocate.")
    public ResponseEntity<ApiResponse<List<TimeSeriesDataPoint>>> getFilingTrend(
            @PathVariable String advocateUuid,
            @RequestParam(defaultValue = "2025") int year) {
        return ResponseEntity.ok(ApiResponse.success(
                "Trend fetched successfully",
                advocateAnalyticsService.getMonthlyFilingTrend(advocateUuid, year)));
    }
}
