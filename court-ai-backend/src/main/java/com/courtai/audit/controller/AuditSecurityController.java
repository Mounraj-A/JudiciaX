package com.courtai.audit.controller;

import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/audit/security")
@RequiredArgsConstructor
@Tag(name = "Audit Module")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUDITOR')")
public class AuditSecurityController {

    @GetMapping("/events")
    @Operation(summary = "Get Security Events")
    public ResponseEntity<ApiResponse<Object>> getSecurityEvents() {
        // Placeholder returning empty list for demo purposes
        return ResponseEntity.ok(ApiResponse.success("Security events retrieved", Collections.emptyList()));
    }
}
