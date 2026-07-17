package com.courtai.audit.controller;

import com.courtai.audit.dto.ComplianceAuditResponse;
import com.courtai.audit.service.AuditComplianceService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit/compliance")
@RequiredArgsConstructor
@Tag(name = "Audit Module")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUDITOR')")
public class AuditComplianceController {

    private final AuditComplianceService auditComplianceService;

    @GetMapping("/violations")
    @Operation(summary = "Get Compliance Violations")
    public ResponseEntity<ApiResponse<List<ComplianceAuditResponse>>> getViolations() {
        List<ComplianceAuditResponse> responses = auditComplianceService.getViolations();
        return ResponseEntity.ok(ApiResponse.success("Compliance violations retrieved successfully", responses));
    }
}
