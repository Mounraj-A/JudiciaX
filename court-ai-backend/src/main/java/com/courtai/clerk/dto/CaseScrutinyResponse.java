package com.courtai.clerk.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Full case detail for clerk scrutiny view. */
@Data
@Builder
public class CaseScrutinyResponse {
    private String uuid;
    private String caseNumber;
    private String officialCaseNumber;
    private String caseTitle;
    private String caseDescription;
    private String caseType;
    private CaseStatus status;
    private CasePriority priority;

    // Parties
    private String petitionerName;
    private String respondentName;
    private String petitionerAdvocateName;
    private String petitionerAdvocateBarNumber;
    private String petitionerAdvocateUuid;

    // Court
    private String courtUuid;
    private String courtName;
    private String courtCode;
    private String district;
    private String state;
    private String caseCategoryName;

    // Legal
    private String policeStation;
    private String actSection;
    private LocalDate filingDate;
    private Integer filingYear;

    // Scrutiny
    private String scrutinyClerkUuid;
    private String verificationRemarks;
    private Boolean isDuplicateChecked;
    private Boolean isJurisdictionVerified;
    private String duplicateCaseUuids;
    private Integer judgeQueuePosition;

    // Registration
    private LocalDateTime registeredAt;
    private String registeredByUuid;

    // Counts
    private long documentCount;
    private long unverifiedDocumentCount;
    private long evidenceCount;
    private long unverifiedEvidenceCount;
    private long openObjectionCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
