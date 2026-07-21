package com.courtai.judge.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Compact case entry for the judge's case list.
 */
@Getter
@Builder
public class JudgeCaseSummaryResponse {

    private String uuid;
    private String caseNumber;
    private String officialCaseNumber;
    private String cnrNumber;
    private String caseTitle;
    private String caseType;
    private CaseStatus status;
    private CasePriority priority;
    private Double priorityScore;
    private String petitionerName;
    private String respondentName;
    private LocalDate filingDate;
    private LocalDate hearingDate;
    private LocalDateTime createdAt;
}
