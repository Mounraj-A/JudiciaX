package com.courtai.admin.controller;

import com.courtai.admin.dto.AdminDashboardResponse;
import com.courtai.admin.service.AdminDashboardServiceV2;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin dashboard controller.
 * <p>Base path: {@code /api/v1/admin/dashboard}</p>
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Dashboard", description = "Admin portal home screen statistics")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final AdminDashboardServiceV2 dashboardService;

    @GetMapping
    @Operation(summary = "Get full admin dashboard",
               description = "Returns all system statistics: users, courts, cases, hearings, "
                           + "AI status, security summary, and recent pending users.")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                "Dashboard loaded", dashboardService.getDashboard()));
    }
}
