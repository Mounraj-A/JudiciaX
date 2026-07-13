package com.courtai.admin.controller;

import com.courtai.admin.dto.AssignClerkRequest;
import com.courtai.admin.service.ClerkAdministrationService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Clerk administration controller.
 * <p>Base path: {@code /api/v1/admin/clerks}</p>
 */
@RestController
@RequestMapping("/admin/clerks")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Clerk Administration", description = "Clerk assignment, transfer, and statistics")
@SecurityRequirement(name = "bearerAuth")
public class ClerkAdministrationController {

    private final ClerkAdministrationService clerkService;

    @PostMapping("/assign")
    @Operation(summary = "Assign a clerk to a court")
    public ResponseEntity<ApiResponse<Void>> assignClerk(
            @Valid @RequestBody AssignClerkRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        clerkService.assignClerkToCourt(request, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Clerk assigned to court"));
    }

    @PutMapping("/{clerkUserUuid}/transfer")
    @Operation(summary = "Transfer a clerk to a different court")
    public ResponseEntity<ApiResponse<Void>> transferClerk(
            @PathVariable String clerkUserUuid,
            @RequestParam String newCourtUuid,
            @RequestParam(required = false, defaultValue = "Administrative transfer") String reason,
            @AuthenticationPrincipal UserPrincipal admin) {
        clerkService.transferClerk(clerkUserUuid, newCourtUuid, reason, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Clerk transferred"));
    }

    @GetMapping("/{clerkUserUuid}/statistics")
    @Operation(summary = "Get clerk workload statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics(
            @PathVariable String clerkUserUuid) {
        return ResponseEntity.ok(ApiResponse.success("Clerk statistics retrieved",
                clerkService.getClerkStatistics(clerkUserUuid)));
    }
}
