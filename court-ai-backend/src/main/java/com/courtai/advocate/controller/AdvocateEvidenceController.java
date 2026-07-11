package com.courtai.advocate.controller;

import com.courtai.advocate.dto.CreateEvidenceRequest;
import com.courtai.advocate.dto.EvidenceResponse;
import com.courtai.advocate.service.AdvocateEvidenceService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for advocate evidence management.
 */
@RestController
@RequestMapping("/advocate/cases/{caseUuid}/evidence")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateEvidenceController {

    private final AdvocateEvidenceService evidenceService;

    @PostMapping
    @Operation(summary = "Submit evidence for a case")
    public ResponseEntity<ApiResponse<EvidenceResponse>> submitEvidence(
            @PathVariable String caseUuid,
            @Valid @RequestBody CreateEvidenceRequest request) {
        EvidenceResponse response = evidenceService.submitEvidence(caseUuid, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Evidence submitted", response));
    }

    @GetMapping
    @Operation(summary = "List evidence for a case")
    public ResponseEntity<ApiResponse<Page<EvidenceResponse>>> getEvidence(
            @PathVariable String caseUuid,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Evidence retrieved",
                evidenceService.getEvidence(caseUuid, pageable)));
    }

    @GetMapping("/{evidenceUuid}")
    @Operation(summary = "Get evidence detail")
    public ResponseEntity<ApiResponse<EvidenceResponse>> getEvidenceByUuid(
            @PathVariable String caseUuid,
            @PathVariable String evidenceUuid) {
        return ResponseEntity.ok(ApiResponse.success("Evidence retrieved",
                evidenceService.getEvidenceByUuid(caseUuid, evidenceUuid)));
    }

    @DeleteMapping("/{evidenceUuid}")
    @Operation(summary = "Delete evidence (before court admission only)")
    public ResponseEntity<ApiResponse<Void>> deleteEvidence(
            @PathVariable String caseUuid,
            @PathVariable String evidenceUuid) {
        evidenceService.deleteEvidence(caseUuid, evidenceUuid);
        return ResponseEntity.ok(ApiResponse.success("Evidence deleted"));
    }
}
