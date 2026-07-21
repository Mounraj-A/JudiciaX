package com.courtai.advocate.controller;

import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.casefile.repository.CaseFileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/advocate/cases/export")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateExportController {

    private final AdvocateSecurityUtil securityUtil;
    private final CaseFileRepository caseFileRepository;

    @GetMapping
    @Operation(summary = "Export advocate cases to CSV/PDF")
    public ResponseEntity<Resource> exportCases(@RequestParam(defaultValue = "csv") String format) {
        var advocate = securityUtil.getCurrentAdvocate();
        
        // In a real implementation, we would query cases and generate a real CSV or PDF using Apache POI or iText.
        // For Phase 1, we just return a simple CSV string to ensure the endpoint exists and frontend can download it.
        String csvContent = "Case Number,Title,Status\nADV-2025-001,Example Case,FILED\n";
        ByteArrayResource resource = new ByteArrayResource(csvContent.getBytes(StandardCharsets.UTF_8));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=my_cases." + format)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
