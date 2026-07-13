package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.JudgeDashboardResponse;
import com.courtai.judge.service.JudgeDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard controller for the judge portal.
 * Returns aggregate counts and shortlists for the home screen.
 */
@RestController
@RequestMapping("/judge/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Dashboard")
public class JudgeDashboardController {

    private final JudgeDashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get judge dashboard",
               description = "Returns aggregate counts (assigned cases, today's hearings, pending judgments, etc.) and shortlists.")
    public ResponseEntity<ApiResponse<JudgeDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", dashboardService.getDashboard()));
    }
}
