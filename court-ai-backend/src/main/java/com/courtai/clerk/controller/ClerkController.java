package com.courtai.clerk.controller;

import com.courtai.clerk.dto.ClerkProfileResponse;
import com.courtai.clerk.service.ClerkService;
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
 * REST controller for clerk profile.
 */
@RestController
@RequestMapping("/clerk")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CLERK')")
@Tag(name = "Clerk Module", description = "Clerk Portal: case scrutiny, registration, and document verification")
public class ClerkController {

    private final ClerkService clerkService;

    @GetMapping("/profile")
    @Operation(summary = "Get my clerk profile")
    public ResponseEntity<ApiResponse<ClerkProfileResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", clerkService.getMyProfile()));
    }
}
