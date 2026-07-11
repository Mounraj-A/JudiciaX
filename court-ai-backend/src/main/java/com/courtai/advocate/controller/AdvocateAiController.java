package com.courtai.advocate.controller;

import com.courtai.advocate.dto.AiAnalysisResponse;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.ai.entity.CaseAnalysis;
import com.courtai.ai.repository.CaseAnalysisRepository;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.dto.ApiResponse;
import com.courtai.exception.UnauthorizedActionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for AI analysis — read-only for advocates.
 * Advocates can view AI scores but CANNOT modify them.
 */
@RestController
@RequestMapping("/advocate/cases/{caseUuid}/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateAiController {

    private final AdvocateSecurityUtil    securityUtil;
    private final CaseFileRepository      caseFileRepository;
    private final CaseAnalysisRepository  caseAnalysisRepository;

    @GetMapping
    @Operation(summary = "View AI analysis for a case (read-only)")
    public ResponseEntity<ApiResponse<AiAnalysisResponse>> getAiAnalysis(
            @PathVariable String caseUuid) {
        var advocate = securityUtil.getCurrentAdvocate();

        // Ownership validation
        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocate.getUuid())) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your account.");
        }

        // Attempt to load analysis
        CaseAnalysis analysis = caseAnalysisRepository.findByCaseFileUuid(caseUuid)
                .orElse(null);

        AiAnalysisResponse response;
        if (analysis == null) {
            response = AiAnalysisResponse.builder()
                    .caseUuid(caseUuid)
                    .analysisAvailable(false)
                    .build();
        } else {
            response = AiAnalysisResponse.builder()
                    .caseUuid(caseUuid)
                    .urgencyScore(analysis.getUrgencyScore())
                    .trustScore(analysis.getTrustScore())
                    .delayImpactScore(analysis.getDelayImpactScore())
                    .confidenceScore(analysis.getConfidenceScore())
                    .recommendation(analysis.getRecommendation())
                    .modelVersion(analysis.getModelVersion())
                    .generatedAt(analysis.getGeneratedAt())
                    .analysisAvailable(true)
                    .build();
        }

        return ResponseEntity.ok(ApiResponse.success("AI analysis retrieved", response));
    }
}
