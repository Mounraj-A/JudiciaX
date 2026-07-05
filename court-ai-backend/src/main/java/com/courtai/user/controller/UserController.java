package com.courtai.user.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.user.dto.CreateUserRequest;
import com.courtai.user.dto.UserResponse;
import com.courtai.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing system users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user account. Requires ADMIN role.")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        log.info("POST /users — creating user with email: [{}]", request.getEmail());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("User created successfully", response));
    }

    @Operation(summary = "Get user by UUID", description = "Retrieves a user by their UUID.")
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_JUDGE', 'ROLE_CLERK')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUuid(
            @PathVariable String uuid) {

        log.debug("GET /users/{} — fetching user", uuid);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userService.getUserByUuid(uuid)));
    }

    @Operation(summary = "Get all users", description = "Retrieves all active users. Requires ADMIN role.")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        log.debug("GET /users — fetching all users");
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userService.getAllUsers()));
    }

    @Operation(summary = "Delete user by UUID", description = "Soft-deletes a user. Requires ADMIN role.")
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String uuid) {

        log.info("DELETE /users/{} — soft-deleting user", uuid);
        userService.deleteUser(uuid);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}
