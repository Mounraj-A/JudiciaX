package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.JudgeAiAnalysisResponse;
import com.courtai.judge.service.JudgeAiReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Read-only AI analysis controller for the judge portal.
 * The judge can review AI recommendations and XAI factors.
 */
@RestController
@RequestMapping("/judge/cases/{caseUuid}/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — AI Analysis Review")
public class JudgeAiController {

    private final JudgeAiReviewService aiReviewService;

    @GetMapping
    @Operation(summary = "Get AI analysis for a case",
               description = "Returns urgency score, delay impact, priority score, trust score, "
                           + "confidence score, AI recommendation, and explainable AI (XAI) factors. Read-only.")
    public ResponseEntity<ApiResponse<JudgeAiAnalysisResponse>> getAiAnalysis(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("AI analysis retrieved",
                aiReviewService.getAiAnalysis(caseUuid)));
    }
}
