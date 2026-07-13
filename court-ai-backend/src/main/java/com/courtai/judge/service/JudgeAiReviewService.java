package com.courtai.judge.service;

import com.courtai.judge.dto.JudgeAiAnalysisResponse;

/**
 * Service contract for read-only AI analysis review by judges.
 */
public interface JudgeAiReviewService {

    /**
     * Returns the AI analysis for a case.
     * Validates that the case is assigned to the current judge.
     * Throws {@link com.courtai.exception.ResourceNotFoundException} if no analysis exists.
     */
    JudgeAiAnalysisResponse getAiAnalysis(String caseUuid);
}
