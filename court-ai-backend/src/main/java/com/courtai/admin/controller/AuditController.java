package com.courtai.admin.controller;

import com.courtai.admin.dto.AuditLogResponse;
import com.courtai.admin.service.AuditManagementService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Audit management controller — search, filter, detail, and export.
 * <p>Base path: {@code /api/v1/admin/audit}</p>
 */
@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Audit", description = "System-wide audit log search and export")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditManagementService auditService;

    @GetMapping
    @Operation(summary = "Search audit logs",
               description = "Filter by action, actorUuid, entityType, and/or outcome. Paginated.")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> search(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String actorUuid,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String outcome,
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "20")         int size) {
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved",
                auditService.searchAuditLogs(action, actorUuid, entityType, outcome,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get audit log detail by UUID")
    public ResponseEntity<ApiResponse<AuditLogResponse>> getDetail(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Audit log retrieved",
                auditService.getAuditLogByUuid(uuid)));
    }

    @GetMapping("/export")
    @Operation(summary = "Export audit logs by date range",
               description = "Returns all audit logs between 'from' and 'to' timestamps. "
                           + "Format: yyyy-MM-dd'T'HH:mm:ss")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> export(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponse.success("Audit logs exported",
                auditService.exportByDateRange(from, to)));
    }
}
