package com.courtai.clerk.controller;

import com.courtai.clerk.dto.ClerkDashboardResponse;
import com.courtai.clerk.service.ClerkDashboardService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the clerk portal dashboard.
 */
@RestController
@RequestMapping("/clerk/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CLERK')")
@Tag(name = "Clerk Module")
public class ClerkDashboardController {

    private final ClerkDashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get clerk dashboard summary")
    public ResponseEntity<ApiResponse<ClerkDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", dashboardService.getDashboard()));
    }
}
