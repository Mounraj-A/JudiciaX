package com.courtai.casemanagement.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Full case details response including status, parties, flags, assignment, and metadata. */
@Getter
@Builder
public class CaseDetailsResponse {

    // ── Core Identifiers ──────────────────────────────────────────────────────
    private String uuid;
    private String caseNumber;
    private String cnrNumber;
    private String officialCaseNumber;
    private Integer filingYear;

    // ── Description ───────────────────────────────────────────────────────────
    private String caseTitle;
    private String caseDescription;
    private CaseType caseType;
    private CaseStatus status;
    private CasePriority priority;
    private Double priorityScore;

    // ── Parties ───────────────────────────────────────────────────────────────
    private String petitionerName;
    private String respondentName;
    private String petitionerAdvocateUuid;
    private String petitionerAdvocateName;
    private String respondentAdvocateUuid;
    private String respondentAdvocateName;

    // ── Court & Assignment ────────────────────────────────────────────────────
    private String courtUuid;
    private String courtName;
    private String assignedJudgeUuid;
    private String assignedJudgeName;
    private String assignedJudgeDesignation;

    // ── Legal Details ─────────────────────────────────────────────────────────
    private String policeStation;
    private String actSection;
    private String caseCategoryName;

    // ── Dates ─────────────────────────────────────────────────────────────────
    private LocalDate filingDate;
    private LocalDate hearingDate;
    private LocalDateTime registeredAt;

    // ── Flags ─────────────────────────────────────────────────────────────────
    private Boolean medicalEmergency;
    private Boolean childInvolved;
    private Boolean womenSafety;
    private Boolean seniorCitizen;
    private Boolean disability;
    private Boolean financialFraud;
    private Boolean cyberCrime;
    private Boolean threatToLife;
    private Boolean highPublicInterest;

    // ── Scrutiny ──────────────────────────────────────────────────────────────
    private String verificationRemarks;
    private Boolean isDuplicateChecked;
    private Boolean isJurisdictionVerified;
    private Integer judgeQueuePosition;

    // ── Audit ─────────────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
