package com.courtai.auth.controller;

import com.courtai.auth.dto.AuthResponse;
import com.courtai.auth.dto.LoginRequest;
import com.courtai.auth.service.AuthService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller — handles login, token refresh, and logout.
 *
 * <p>Base path: {@code /api/v1/auth}</p>
 *
 * <p>All endpoints in this controller are publicly accessible (no JWT required).
 * Configured in {@link com.courtai.config.SecurityConfig}.</p>
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for authentication and token management")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticates user credentials and returns JWT tokens.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("POST /auth/login — login attempt for: [{}]", request.getEmail());
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @Operation(summary = "Refresh Token", description = "Issues a new access token using a valid refresh token.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken) {

        log.debug("POST /auth/refresh — token refresh requested");
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }

    @Operation(summary = "Logout", description = "Invalidates the current session token.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authorizationHeader) {

        log.info("POST /auth/logout");
        String token = authorizationHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
