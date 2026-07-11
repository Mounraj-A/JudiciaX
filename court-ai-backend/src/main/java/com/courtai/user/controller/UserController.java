package com.courtai.user.controller;

import com.courtai.auth.dto.request.ChangePasswordRequest;
import com.courtai.auth.dto.response.SessionResponse;
import com.courtai.auth.dto.response.UserProfileResponse;
import com.courtai.common.constants.ApiConstants;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import com.courtai.user.dto.CreateUserRequest;
import com.courtai.user.dto.PrivacySettingsRequest;
import com.courtai.user.dto.UpdateProfileRequest;
import com.courtai.user.dto.UserResponse;
import com.courtai.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management operations.
 *
 * <p>Base path: {@code /api/v1/users}</p>
 *
 * <p>Follows controller best practices:</p>
 * <ul>
 *   <li>No business logic — delegates to {@link UserService}</li>
 *   <li>Constructor injection via {@code @RequiredArgsConstructor}</li>
 *   <li>Role-based access via {@code @PreAuthorize}</li>
 *   <li>Always returns {@link ApiResponse} wrapper</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping(ApiConstants.API_USER_BASE)
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Profile management and session control")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // =========================================================
    //  ADMIN — USER MANAGEMENT (existing)
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new user (Admin)", description = "Creates a new user. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("POST /users — creating user with email: [{}]", request.getEmail());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("User created successfully", response));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_JUDGE', 'ROLE_CLERK')")
    @Operation(summary = "Get user by UUID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_RETRIEVED, userService.getUserByUuid(uuid)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all users (Admin)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USERS_RETRIEVED, userService.getAllUsers()));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Soft-delete user (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_DELETED));
    }

    // =========================================================
    //  SELF-SERVICE — PROFILE
    // =========================================================

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        UserProfileResponse response = userService.getMyProfile(principal);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_PROFILE_RETRIEVED, response));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UserProfileResponse response = userService.updateMyProfile(principal, request);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_PROFILE_UPDATED, response));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change my password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        userService.changePassword(principal, request);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_CHANGE_PASSWORD_SUCCESS));
    }

    // =========================================================
    //  SELF-SERVICE — SESSIONS
    // =========================================================

    @GetMapping("/me/sessions")
    @Operation(summary = "List my active sessions")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getMySessions(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpRequest) {
        String accessToken = httpRequest.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        List<SessionResponse> sessions = userService.getMySessions(principal, accessToken);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_SESSIONS_RETRIEVED, sessions));
    }

    @DeleteMapping("/me/sessions/{sessionUuid}")
    @Operation(summary = "Revoke a specific session")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @PathVariable String sessionUuid,
            @AuthenticationPrincipal UserPrincipal principal) {
        userService.revokeSession(principal, sessionUuid);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_SESSION_REVOKED));
    }

    @DeleteMapping("/me/sessions")
    @Operation(summary = "Revoke all my active sessions")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions(
            @AuthenticationPrincipal UserPrincipal principal) {
        userService.revokeAllSessions(principal);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_ALL_SESSIONS_REVOKED));
    }

    // =========================================================
    //  SELF-SERVICE — PRIVACY
    // =========================================================

    @PutMapping("/me/privacy-settings")
    @Operation(summary = "Update my privacy and notification settings")
    public ResponseEntity<ApiResponse<Void>> updatePrivacySettings(
            @RequestBody PrivacySettingsRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        userService.updatePrivacySettings(principal, request);
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_PRIVACY_SETTINGS_UPDATED));
    }
}
