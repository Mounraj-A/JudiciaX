package com.courtai.casemanagement.controller;

import com.courtai.casemanagement.dto.CaseSearchRequest;
import com.courtai.casemanagement.dto.CaseSummaryResponse;
import com.courtai.casemanagement.dto.CaseStatisticsResponse;
import com.courtai.casemanagement.service.CaseSearchService;
import com.courtai.casemanagement.service.CaseStatisticsService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Advanced multi-dimensional case search and statistics.
 * <p>Base path: {@code /api/v1/cases/search}</p>
 */
@RestController
@RequestMapping("/cases/search")
@RequiredArgsConstructor
@Tag(name = "Case Management", description = "Advanced search, filtering, and statistics")
@SecurityRequirement(name = "bearerAuth")
public class CaseSearchController {

    private final CaseSearchService     searchService;
    private final CaseStatisticsService statisticsService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Advanced case search",
               description = "Multi-dimensional case search with optional filters for status, priority, "
                           + "court, judge, advocate, party names, date range, keyword, and AI score. "
                           + "All fields optional. Supports pagination and sorting.")
    public ResponseEntity<ApiResponse<Page<CaseSummaryResponse>>> search(
            @RequestBody CaseSearchRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Search results",
                searchService.search(request)));
    }

    // ── Statistics ────────────────────────────────────────────────────────────

    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get global case statistics")
    public ResponseEntity<ApiResponse<CaseStatisticsResponse>> globalStatistics() {
        return ResponseEntity.ok(ApiResponse.success("Global statistics",
                statisticsService.getGlobalStatistics()));
    }

    @GetMapping("/statistics/court/{courtUuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_JUDGE','ROLE_CLERK')")
    @Operation(summary = "Get case statistics for a specific court")
    public ResponseEntity<ApiResponse<CaseStatisticsResponse>> statisticsByCourt(
            @PathVariable String courtUuid) {
        return ResponseEntity.ok(ApiResponse.success("Court statistics",
                statisticsService.getStatisticsByCourt(courtUuid)));
    }

    @GetMapping("/statistics/judge")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_JUDGE')")
    @Operation(summary = "Get statistics for the authenticated judge's assigned cases")
    public ResponseEntity<ApiResponse<CaseStatisticsResponse>> statisticsByJudge(
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Judge statistics",
                statisticsService.getStatisticsByJudge(actor.getUserUuid())));
    }

    @GetMapping("/statistics/advocate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADVOCATE','ROLE_ADMIN')")
    @Operation(summary = "Get statistics for the authenticated advocate's cases")
    public ResponseEntity<ApiResponse<CaseStatisticsResponse>> statisticsByAdvocate(
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Advocate statistics",
                statisticsService.getStatisticsByAdvocate(actor.getUserUuid())));
    }
}
