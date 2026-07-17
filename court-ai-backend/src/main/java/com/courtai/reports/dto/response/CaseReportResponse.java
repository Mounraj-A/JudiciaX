package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Full lifecycle report for a single case file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseReportResponse {

    // ── Identifiers ───────────────────────────────────────────────────────

    private String caseUuid;
    private String caseNumber;
    private String officialCaseNumber;
    private String cnrNumber;
    private String caseTitle;
    private String caseDescription;

    // ── Classification ────────────────────────────────────────────────────

    private String caseType;
    private String categoryName;
    private String status;
    private String priority;

    // ── AI Scores ─────────────────────────────────────────────────────────

    private Double priorityScore;
    private Double urgencyScore;
    private Double delayImpactScore;
    private Double trustScore;
    private Double aiConfidenceScore;
    private String aiRecommendation;

    // ── People ────────────────────────────────────────────────────────────

    private String courtName;
    private String courtCode;
    private String state;
    private String district;
    private String judgeName;
    private String petitionerName;
    private String respondentName;
    private String petitionerAdvocateName;
    private String respondentAdvocateName;

    // ── Dates ─────────────────────────────────────────────────────────────

    private LocalDate filingDate;
    private LocalDateTime registeredAt;
    private LocalDate hearingDate;
    private Integer filingYear;

    // ── Lifecycle Metrics ─────────────────────────────────────────────────

    /** Number of calendar days since filing date. */
    private Long ageInDays;

    /** Number of days from filing to registration (scrutiny time). */
    private Long registrationDays;

    // ── Document & Evidence ───────────────────────────────────────────────

    private long totalDocuments;
    private long verifiedDocuments;
    private long totalEvidence;
    private long verifiedEvidence;

    // ── Hearing Stats ─────────────────────────────────────────────────────

    private long totalHearings;
    private long completedHearings;
    private long adjournedHearings;

    // ── Clerk Verification ────────────────────────────────────────────────

    private Boolean isDuplicateChecked;
    private Boolean isJurisdictionVerified;
    private String duplicateCaseUuids;

    // ── Timeline ──────────────────────────────────────────────────────────

    /** Ordered list of status transitions. */
    private List<GraphDataPoint> statusTimeline;
}
