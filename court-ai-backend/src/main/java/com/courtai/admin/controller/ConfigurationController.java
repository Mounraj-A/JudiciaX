package com.courtai.admin.controller;

import com.courtai.admin.dto.ConfigurationRequest;
import com.courtai.admin.dto.ConfigurationResponse;
import com.courtai.admin.service.ConfigurationService;
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
 * System configuration controller.
 * <p>Base path: {@code /api/v1/admin/configurations}</p>
 */
@RestController
@RequestMapping("/admin/configurations")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Configuration", description = "Runtime system configuration management")
@SecurityRequirement(name = "bearerAuth")
public class ConfigurationController {

    private final ConfigurationService configService;

    @GetMapping
    @Operation(summary = "List all configuration entries (paginated)")
    public ResponseEntity<ApiResponse<Page<ConfigurationResponse>>> getAll(
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "50")         int size,
            @RequestParam(defaultValue = "configKey")  String sortBy) {
        return ResponseEntity.ok(ApiResponse.success("Configurations retrieved",
                configService.getAll(PageRequest.of(page, size, Sort.by(sortBy)))));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get configuration by UUID")
    public ResponseEntity<ApiResponse<ConfigurationResponse>> getByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Configuration retrieved",
                configService.getByUuid(uuid)));
    }

    @GetMapping("/key/{key}")
    @Operation(summary = "Get configuration by key")
    public ResponseEntity<ApiResponse<ConfigurationResponse>> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(ApiResponse.success("Configuration retrieved",
                configService.getByKey(key)));
    }

    @PostMapping
    @Operation(summary = "Create a new configuration entry",
               description = "Validates unique config key. Prevents duplicate keys.")
    public ResponseEntity<ApiResponse<ConfigurationResponse>> create(
            @Valid @RequestBody ConfigurationRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Configuration created",
                        configService.create(request, admin.getUserUuid())));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a configuration entry",
               description = "Read-only configs (isEditable=false) cannot be modified.")
    public ResponseEntity<ApiResponse<ConfigurationResponse>> update(
            @PathVariable String uuid,
            @RequestBody ConfigurationRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(ApiResponse.success("Configuration updated",
                configService.update(uuid, request, admin.getUserUuid())));
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a configuration entry",
               description = "Cannot delete read-only configs.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        configService.delete(uuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Configuration deleted"));
    }
}
