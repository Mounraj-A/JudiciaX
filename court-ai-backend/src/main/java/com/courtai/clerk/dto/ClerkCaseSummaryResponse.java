package com.courtai.clerk.dto;

import com.courtai.common.enums.CaseStatus;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Compact case row for paginated list views in the clerk portal. */
@Data
@Builder
public class ClerkCaseSummaryResponse {
    private String uuid;
    private String caseNumber;
    private String officialCaseNumber;
    private String caseTitle;
    private String caseType;
    private CaseStatus status;
    private String petitionerName;
    private String respondentName;
    private String petitionerAdvocateName;
    private String courtName;
    private LocalDate filingDate;
    private long openObjectionCount;
    private Boolean isDuplicateChecked;
    private LocalDateTime createdAt;
}
