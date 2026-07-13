package com.courtai.admin.controller;

import com.courtai.admin.dto.SecurityEventResponse;
import com.courtai.admin.service.SecurityMonitoringService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Security monitoring controller — events, sessions, revocations.
 * <p>Base path: {@code /api/v1/admin/security}</p>
 */
@RestController
@RequestMapping("/admin/security")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Security Monitoring", description = "Security events, sessions, and revocations")
@SecurityRequirement(name = "bearerAuth")
public class SecurityController {

    private final SecurityMonitoringService securityService;

    @GetMapping("/events")
    @Operation(summary = "List all security events (paginated)")
    public ResponseEntity<ApiResponse<Page<SecurityEventResponse>>> getEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SecurityEventResponse> result = (eventType != null && !eventType.isBlank())
                ? securityService.getSecurityEventsByType(eventType,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                : securityService.getSecurityEvents(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.success("Security events retrieved", result));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get security summary",
               description = "Returns active sessions, failed logins today, account locks today, "
                           + "and suspicious logins today.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success("Security summary retrieved",
                securityService.getSecuritySummary()));
    }

    @DeleteMapping("/sessions/{sessionUuid}")
    @Operation(summary = "Revoke a specific user session",
               description = "Marks the session as REVOKED, forcing re-login.")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @PathVariable String sessionUuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        securityService.revokeUserSession(sessionUuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Session revoked"));
    }

    @DeleteMapping("/sessions/user/{userUuid}")
    @Operation(summary = "Revoke all sessions for a user",
               description = "Forces the user to re-login from all devices.")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions(
            @PathVariable String userUuid,
            @AuthenticationPrincipal UserPrincipal admin) {
        securityService.revokeAllUserSessions(userUuid, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("All sessions revoked"));
    }
}
