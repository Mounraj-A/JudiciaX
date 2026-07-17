package com.courtai.audit.controller;

import com.courtai.audit.dto.AuditExportRequest;
import com.courtai.audit.service.AuditExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit/export")
@RequiredArgsConstructor
@Tag(name = "Audit Module")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUDITOR')")
public class AuditExportController {

    private final AuditExportService auditExportService;

    @PostMapping
    @Operation(summary = "Export Audit Logs")
    public ResponseEntity<byte[]> exportLogs(@RequestBody AuditExportRequest request) {
        byte[] data = auditExportService.exportAuditLogs(request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "audit_export." + request.getFormat().toLowerCase());

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}
