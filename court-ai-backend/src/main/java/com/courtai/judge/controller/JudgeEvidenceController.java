package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.JudgeEvidenceResponse;
import com.courtai.judge.service.JudgeEvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only evidence review controller for the judge portal.
 */
@RestController
@RequestMapping("/judge/cases/{caseUuid}/evidence")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Evidence Review")
public class JudgeEvidenceController {

    private final JudgeEvidenceService evidenceService;

    @GetMapping
    @Operation(summary = "List case evidence",
               description = "Returns all evidence items for a case assigned to the current judge.")
    public ResponseEntity<ApiResponse<List<JudgeEvidenceResponse>>> getEvidence(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Evidence retrieved",
                evidenceService.getEvidenceByCase(caseUuid)));
    }

    @GetMapping("/{evidenceUuid}")
    @Operation(summary = "Get evidence detail")
    public ResponseEntity<ApiResponse<JudgeEvidenceResponse>> getEvidenceItem(
            @PathVariable String caseUuid,
            @PathVariable String evidenceUuid) {
        return ResponseEntity.ok(ApiResponse.success("Evidence item retrieved",
                evidenceService.getEvidenceByUuid(caseUuid, evidenceUuid)));
    }
}
