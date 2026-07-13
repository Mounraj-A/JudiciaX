package com.courtai.casemanagement.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Compact case response for list views and search results. */
@Getter
@Builder
public class CaseSummaryResponse {
    private String uuid;
    private String caseNumber;
    private String cnrNumber;
    private String officialCaseNumber;
    private String caseTitle;
    private CaseType caseType;
    private CaseStatus status;
    private CasePriority priority;
    private Double priorityScore;
    private String petitionerName;
    private String respondentName;
    private String courtName;
    private String assignedJudgeName;
    private LocalDate filingDate;
    private LocalDate hearingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
