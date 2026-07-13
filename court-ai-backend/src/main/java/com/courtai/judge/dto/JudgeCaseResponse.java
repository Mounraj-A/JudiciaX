package com.courtai.judge.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Full case detail response for the judge — read-only view of a single case.
 * Does not expose internal DB IDs.
 */
@Getter
@Builder
public class JudgeCaseResponse {

    // ── Identifiers ───────────────────────────────────────────────────────
    private String uuid;
    private String caseNumber;
    private String officialCaseNumber;
    private String cnrNumber;
    private Integer filingYear;

    // ── Case Description ──────────────────────────────────────────────────
    private String caseTitle;
    private String caseDescription;
    private CaseType caseType;
    private CaseStatus status;
    private CasePriority priority;
    private Double priorityScore;

    // ── Dates ─────────────────────────────────────────────────────────────
    private LocalDate filingDate;
    private LocalDate hearingDate;
    private LocalDateTime registeredAt;

    // ── Parties ───────────────────────────────────────────────────────────
    private String petitionerName;
    private String respondentName;
    private String courtName;

    // ── Legal ─────────────────────────────────────────────────────────────
    private String policeStation;
    private String actSection;

    // ── Clerk Info ────────────────────────────────────────────────────────
    private String verificationRemarks;

    // ── Audit ─────────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
