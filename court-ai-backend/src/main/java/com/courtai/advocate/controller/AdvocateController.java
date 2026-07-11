package com.courtai.advocate.controller;

import com.courtai.advocate.dto.AdvocateDashboardResponse;
import com.courtai.advocate.dto.AdvocateProfileResponse;
import com.courtai.advocate.dto.UpdateAdvocateProfileRequest;
import com.courtai.advocate.service.AdvocateDashboardService;
import com.courtai.advocate.service.AdvocateService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for advocate profile and dashboard.
 *
 * <p>All endpoints require {@code ROLE_ADVOCATE} authority.</p>
 */
@RestController
@RequestMapping("/advocate")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module", description = "Advocate profile, dashboard, and case management APIs")
public class AdvocateController {

    private final AdvocateService       advocateService;
    private final AdvocateDashboardService dashboardService;

    @GetMapping("/profile")
    @Operation(summary = "Get my advocate profile")
    public ResponseEntity<ApiResponse<AdvocateProfileResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", advocateService.getMyProfile()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my advocate profile")
    public ResponseEntity<ApiResponse<AdvocateProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateAdvocateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", advocateService.updateMyProfile(request)));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get advocate dashboard summary")
    public ResponseEntity<ApiResponse<AdvocateDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", dashboardService.getDashboard()));
    }
}
