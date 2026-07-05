package com.courtai.admin.controller;

import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin controller — system-level administrative operations.
 *
 * <p>Base path: {@code /api/v1/admin}</p>
 * <p>All endpoints require {@code ROLE_ADMIN}.</p>
 *
 * <p>Full CRUD and administrative operations will be implemented in Phase 2.</p>
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Admin", description = "System administration APIs")
public class AdminController {

    @Operation(summary = "Admin health check", description = "Verifies admin endpoint access. Requires ROLE_ADMIN.")
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<Map<String, String>>> ping() {
        log.debug("GET /admin/ping");
        return ResponseEntity.ok(ApiResponse.success(
                "Admin endpoint accessible",
                Map.of("status", "UP", "module", "admin")
        ));
    }
}
