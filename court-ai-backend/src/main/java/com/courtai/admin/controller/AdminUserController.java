package com.courtai.admin.controller;

import com.courtai.admin.service.AdminDashboardService;
import com.courtai.admin.service.AdminUserService;
import com.courtai.auth.dto.response.UserProfileResponse;
import com.courtai.common.constants.ApiConstants;
import com.courtai.common.dto.ApiResponse;
import com.courtai.common.enums.UserRole;
import com.courtai.security.UserPrincipal;
import com.courtai.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin REST controller for user management and system monitoring.
 *
 * <p>Base path: {@code /api/v1/admin}</p>
 * <p>All endpoints require {@code ROLE_ADMIN}.</p>
 */
@Slf4j
@RestController
@RequestMapping(ApiConstants.API_ADMIN_BASE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin — User Management", description = "Admin operations for user approval, lock, role management")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService      adminUserService;
    private final AdminDashboardService adminDashboardService;

    // =========================================================
    //  USER LISTING
    // =========================================================

    @GetMapping("/users")
    @Operation(summary = "List all users (paginated)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponse> users = adminUserService.getAllUsers(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USERS_RETRIEVED, users));
    }

    @GetMapping("/users/{uuid}")
    @Operation(summary = "Get full user profile by UUID")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_RETRIEVED,
                adminUserService.getUserByUuid(uuid)));
    }

    // =========================================================
    //  APPROVAL WORKFLOW
    // =========================================================

    @PostMapping("/users/{uuid}/approve")
    @Operation(summary = "Approve a pending user account")
    public ResponseEntity<ApiResponse<Void>> approveUser(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminUserService.approveUser(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_APPROVED));
    }

    @PostMapping("/users/{uuid}/reject")
    @Operation(summary = "Reject a pending user account")
    public ResponseEntity<ApiResponse<Void>> rejectUser(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "Identity verification failed") String reason,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminUserService.rejectUser(uuid, reason, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_REJECTED));
    }

    // =========================================================
    //  LOCK / UNLOCK
    // =========================================================

    @PostMapping("/users/{uuid}/lock")
    @Operation(summary = "Manually lock a user account")
    public ResponseEntity<ApiResponse<Void>> lockUser(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "Suspicious activity") String reason,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminUserService.lockUser(uuid, reason, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_LOCKED));
    }

    @PostMapping("/users/{uuid}/unlock")
    @Operation(summary = "Unlock a locked/suspended user account")
    public ResponseEntity<ApiResponse<Void>> unlockUser(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminUserService.unlockUser(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_UNLOCKED));
    }

    // =========================================================
    //  PASSWORD RESET
    // =========================================================

    @PostMapping("/users/{uuid}/reset-password")
    @Operation(summary = "Initiate a password reset for a user")
    public ResponseEntity<ApiResponse<String>> adminResetPassword(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        String token = adminUserService.adminResetPassword(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Password reset token generated", token));
    }

    // =========================================================
    //  ROLE ASSIGNMENT
    // =========================================================

    @PutMapping("/users/{uuid}/assign-role")
    @Operation(summary = "Assign a new role to a user")
    public ResponseEntity<ApiResponse<Void>> assignRole(
            @PathVariable String uuid,
            @RequestParam UserRole role,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminUserService.assignRole(uuid, role, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_ROLE_ASSIGNED));
    }

    // =========================================================
    //  SOFT DELETE
    // =========================================================

    @DeleteMapping("/users/{uuid}")
    @Operation(summary = "Soft-delete a user account")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminUserService.deleteUser(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_USER_DELETED));
    }

    // =========================================================
    //  DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.MSG_DASHBOARD_RETRIEVED,
                adminDashboardService.getDashboardStats()));
    }
}
