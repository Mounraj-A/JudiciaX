package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.request.ExportRequest;
import com.courtai.reports.dto.response.ExportResponse;
import com.courtai.reports.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for generic data export (JSON/CSV/Excel).
 */
@RestController
@RequestMapping("/api/reports/export")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Reports: Export", description = "Export APIs for CSV, EXCEL, and JSON formats")
public class ExportController {

    private final ExportService exportService;

    @PostMapping
    @Operation(summary = "Export report data", description = "Dynamically exports a requested report type to JSON, CSV, or EXCEL.")
    public ResponseEntity<ApiResponse<ExportResponse>> exportData(@RequestBody ExportRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Export processed successfully",
                exportService.processExportRequest(request)));
    }
}
