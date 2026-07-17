package com.courtai.audit.controller;

import com.courtai.audit.dto.AuditResponse;
import com.courtai.audit.dto.AuditSearchRequest;
import com.courtai.audit.service.AuditSearchService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit/search")
@RequiredArgsConstructor
@Tag(name = "Audit Module")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUDITOR')")
public class AuditSearchController {

    private final AuditSearchService auditSearchService;

    @PostMapping
    @Operation(summary = "Advanced Search for Audit Events")
    public ResponseEntity<ApiResponse<Page<AuditResponse>>> search(@RequestBody AuditSearchRequest request, Pageable pageable) {
        Page<AuditResponse> response = auditSearchService.search(request, pageable);
        return ResponseEntity.ok(ApiResponse.success("Audit events retrieved successfully", response));
    }
}
