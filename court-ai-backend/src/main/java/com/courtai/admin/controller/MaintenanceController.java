package com.courtai.admin.controller;

import com.courtai.admin.dto.MaintenanceRequest;
import com.courtai.admin.dto.MaintenanceResponse;
import com.courtai.admin.service.MaintenanceService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Maintenance window controller.
 * <p>Base path: {@code /api/v1/admin/maintenance}</p>
 */
@RestController
@RequestMapping("/admin/maintenance")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Maintenance", description = "Planned maintenance window management")
@SecurityRequirement(name = "bearerAuth")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    @Operation(summary = "List all maintenance windows (paginated)")
    public ResponseEntity<ApiResponse<Page<MaintenanceResponse>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Maintenance windows retrieved",
                maintenanceService.getAll(PageRequest.of(page, size,
                        Sort.by("startTime").descending()))));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get maintenance window by UUID")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> getByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Maintenance window retrieved",
                maintenanceService.getByUuid(uuid)));
    }

    @PostMapping
    @Operation(summary = "Schedule a maintenance window",
               description = "Validates end > start. Prevents overlapping SCHEDULED/ACTIVE windows.")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> create(
            @Valid @RequestBody MaintenanceRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Maintenance window scheduled",
                        maintenanceService.create(request, admin.getUserUuid())));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a maintenance window")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> update(
            @PathVariable String uuid,
            @RequestBody MaintenanceRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Maintenance window updated",
                maintenanceService.update(uuid, request, admin.getUserUuid())));
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a maintenance window",
               description = "Cannot delete an active window. Cancel it first.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        maintenanceService.delete(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Maintenance window deleted"));
    }

    @PutMapping("/{uuid}/activate")
    @Operation(summary = "Activate a scheduled maintenance window")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> activate(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Maintenance window activated",
                maintenanceService.activate(uuid, admin.getUserUuid())));
    }

    @PutMapping("/{uuid}/complete")
    @Operation(summary = "Mark a maintenance window as completed")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> complete(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Maintenance window completed",
                maintenanceService.complete(uuid, admin.getUserUuid())));
    }

    @PutMapping("/{uuid}/cancel")
    @Operation(summary = "Cancel a maintenance window")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> cancel(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Maintenance window cancelled",
                maintenanceService.cancel(uuid, admin.getUserUuid())));
    }
}
