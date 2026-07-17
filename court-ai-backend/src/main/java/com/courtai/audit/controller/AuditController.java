package com.courtai.audit.controller;

import com.courtai.audit.dto.AuditResponse;
import com.courtai.audit.dto.AuditTimelineResponse;
import com.courtai.audit.service.AuditSearchService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("auditModuleController")
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Module", description = "Endpoints for managing system audit logs and timelines")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUDITOR')")
public class AuditController {

    private final AuditSearchService auditSearchService;

    @GetMapping("/{uuid}")
    @Operation(summary = "Get Audit Event Details")
    public ResponseEntity<ApiResponse<AuditResponse>> getAuditDetails(@PathVariable String uuid) {
        AuditResponse response = auditSearchService.getDetails(uuid);
        return ResponseEntity.ok(ApiResponse.success("Audit event details retrieved successfully", response));
    }

    @GetMapping("/timeline/{correlationId}")
    @Operation(summary = "Get Audit Timeline")
    public ResponseEntity<ApiResponse<List<AuditTimelineResponse>>> getTimeline(@PathVariable String correlationId) {
        List<AuditTimelineResponse> timeline = auditSearchService.getTimeline(correlationId);
        return ResponseEntity.ok(ApiResponse.success("Audit timeline retrieved successfully", timeline));
    }
}
