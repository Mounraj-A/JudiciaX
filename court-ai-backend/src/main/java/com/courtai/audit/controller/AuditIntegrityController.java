package com.courtai.audit.controller;

import com.courtai.audit.service.AuditIntegrityService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit/integrity")
@RequiredArgsConstructor
@Tag(name = "Audit Module")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUDITOR')")
public class AuditIntegrityController {

    private final AuditIntegrityService auditIntegrityService;

    @GetMapping("/verify/{uuid}")
    @Operation(summary = "Verify Hash for an Event")
    public ResponseEntity<ApiResponse<Boolean>> verifyHash(@PathVariable String uuid) {
        boolean isVerified = auditIntegrityService.verifyIntegrity(uuid);
        return ResponseEntity.ok(ApiResponse.success("Integrity check completed", isVerified));
    }

    @GetMapping("/verify-chain")
    @Operation(summary = "Verify Hash Chain for entire log")
    public ResponseEntity<ApiResponse<Boolean>> verifyChain() {
        boolean isVerified = auditIntegrityService.verifyChain();
        return ResponseEntity.ok(ApiResponse.success("Hash chain verification completed", isVerified));
    }
}
