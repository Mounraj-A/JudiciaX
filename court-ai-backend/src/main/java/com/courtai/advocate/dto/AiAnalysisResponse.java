package com.courtai.advocate.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Read-only AI analysis response for the Advocate Portal.
 * Advocates can view but never modify AI scores or recommendations.
 */
@Getter
@Builder
public class AiAnalysisResponse {

    private String caseUuid;

    /** AI-computed urgency score (0–100). Higher = more time-critical. */
    private Double urgencyScore;

    /** Trustworthiness/reliability score of submitted documents (0–100). */
    private Double trustScore;

    /** Score representing delay impact on justice (0–100). */
    private Double delayImpactScore;

    /** Confidence level of the AI model in its analysis (0–100). */
    private Double confidenceScore;

    /** Human-readable AI recommendation. */
    private String recommendation;

    /** Version of the AI model that generated this analysis. */
    private String modelVersion;

    /** Timestamp when the AI generated this result. */
    private LocalDateTime generatedAt;

    /** Whether AI analysis has been run for this case. */
    private boolean analysisAvailable;
}
