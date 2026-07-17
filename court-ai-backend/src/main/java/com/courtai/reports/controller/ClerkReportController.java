package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.ClerkReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.service.ClerkAnalyticsService;
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
 * REST controller for clerk analytics.
 */
@RestController
@RequestMapping("/api/reports/clerks")
@RequiredArgsConstructor
@Tag(name = "Reports: Clerks", description = "Clerk performance and analytics")
public class ClerkReportController {

    private final ClerkAnalyticsService clerkAnalyticsService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get all clerk reports", description = "Paginated list of clerks with scrutiny metrics.")
    public ResponseEntity<ApiResponse<Page<ClerkReportResponse>>> getAllClerkReports(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Clerk reports fetched successfully",
                clerkAnalyticsService.getAllClerkReports(pageable)));
    }

    @GetMapping("/{clerkUuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get detailed clerk report", description = "Analytics for a specific clerk.")
    public ResponseEntity<ApiResponse<ClerkReportResponse>> getClerkReport(@PathVariable String clerkUuid) {
        return ResponseEntity.ok(ApiResponse.success(
                "Clerk report fetched successfully",
                clerkAnalyticsService.getClerkReport(clerkUuid)));
    }

    @GetMapping("/duplicate-detection")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get duplicate detection breakdown", description = "Number of duplicate cases detected per court.")
    public ResponseEntity<ApiResponse<List<GraphDataPoint>>> getDuplicateDetectionByCourt() {
        return ResponseEntity.ok(ApiResponse.success(
                "Duplicate detection stats fetched successfully",
                clerkAnalyticsService.getDuplicateDetectionByCourt()));
    }
}
