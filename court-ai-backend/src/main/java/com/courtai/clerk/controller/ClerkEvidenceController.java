package com.courtai.clerk.controller;

import com.courtai.clerk.dto.EvidenceVerificationResponse;
import com.courtai.clerk.dto.VerifyEvidenceRequest;
import com.courtai.clerk.service.ClerkEvidenceVerificationService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for clerk evidence verification.
 */
@RestController
@RequestMapping("/clerk/cases/{caseUuid}/evidence")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CLERK')")
@Tag(name = "Clerk Module")
public class ClerkEvidenceController {

    private final ClerkEvidenceVerificationService evidenceService;

    @GetMapping
    @Operation(summary = "List all evidence for a case")
    public ResponseEntity<ApiResponse<Page<EvidenceVerificationResponse>>> getEvidence(
            @PathVariable String caseUuid,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Evidence retrieved",
                evidenceService.getEvidence(caseUuid, pageable)));
    }

    @GetMapping("/{evidenceUuid}")
    @Operation(summary = "Get evidence detail")
    public ResponseEntity<ApiResponse<EvidenceVerificationResponse>> getEvidenceByUuid(
            @PathVariable String caseUuid,
            @PathVariable String evidenceUuid) {
        return ResponseEntity.ok(ApiResponse.success("Evidence retrieved",
                evidenceService.getEvidenceByUuid(caseUuid, evidenceUuid)));
    }

    @PutMapping("/{evidenceUuid}/verify")
    @Operation(summary = "Verify or reject an evidence item")
    public ResponseEntity<ApiResponse<EvidenceVerificationResponse>> verifyEvidence(
            @PathVariable String caseUuid,
            @PathVariable String evidenceUuid,
            @Valid @RequestBody VerifyEvidenceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                request.isApproved() ? "Evidence verified" : "Evidence rejected",
                evidenceService.verifyEvidence(caseUuid, evidenceUuid, request)));
    }
}
