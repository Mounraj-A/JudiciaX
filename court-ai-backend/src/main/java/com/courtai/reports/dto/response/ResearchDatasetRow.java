package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single row in the research dataset export.
 *
 * <p>Each row represents one case with all its AI and judicial features.
 * This dataset is used to train and evaluate AI models for judicial prioritization.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchDatasetRow {

    // ── Case Identifiers ──────────────────────────────────────────────────

    private String caseUuid;
    private String caseNumber;
    private String caseType;
    private String categoryName;
    private String status;
    private String priority;
    private Integer filingYear;

    // ── AI Feature Scores ─────────────────────────────────────────────────

    /** AI-computed priority/urgency score (0–100). */
    private Double priorityScore;

    /** AI-computed urgency score (0–100). */
    private Double urgencyScore;

    /** AI-computed delay impact score (0–100). */
    private Double delayImpactScore;

    /** AI-computed trust score (0–100). */
    private Double trustScore;

    /** AI model confidence (0–100). */
    private Double confidenceScore;

    // ── Document Features ─────────────────────────────────────────────────

    private long totalDocuments;
    private long verifiedDocuments;

    /** Ratio of verified to total documents (0.0–1.0). */
    private Double documentCompletenessRate;

    // ── Evidence Features ─────────────────────────────────────────────────

    private long totalEvidence;
    private long verifiedEvidence;

    /** Ratio of verified to total evidence (0.0–1.0). */
    private Double evidenceCompletenessRate;

    // ── Clerk Verification Features ───────────────────────────────────────

    private Boolean isDuplicateChecked;
    private Boolean isJurisdictionVerified;
    private Boolean hasDuplicateFlag;

    // ── Lifecycle Features ────────────────────────────────────────────────

    /** Age of the case in days (from filing date to today). */
    private Long ageInDays;

    /** Number of days from filing to registration. */
    private Long registrationDays;

    /** Total number of hearings scheduled. */
    private long hearingCount;

    /** Number of adjournments. */
    private long adjournmentCount;

    // ── AI Recommendation ─────────────────────────────────────────────────

    private String aiRecommendation;

    // ── Court / Judge Context ─────────────────────────────────────────────

    private String courtCode;
    private String state;
    private String district;
    private String courtType;
}
