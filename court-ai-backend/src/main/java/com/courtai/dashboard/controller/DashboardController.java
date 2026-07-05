package com.courtai.dashboard.controller;

import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Dashboard controller — summary statistics and KPI data.
 *
 * <p>Base path: {@code /api/v1/dashboard}</p>
 *
 * <p>Full dashboard analytics will be implemented in Phase 2 with AI-driven insights.</p>
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard and analytics APIs")
public class DashboardController {

    @Operation(summary = "Dashboard overview", description = "Returns summary statistics for the current user's role.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        log.debug("GET /dashboard");
        // Phase 2: Return role-specific KPIs, case counts, pending hearings, etc.
        return ResponseEntity.ok(ApiResponse.success(
                "Dashboard data retrieved",
                Map.of(
                        "message", "Dashboard analytics will be available in Phase 2",
                        "status", "FOUNDATION_READY"
                )
        ));
    }
}
