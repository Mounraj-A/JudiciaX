package com.courtai.advocate.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Compact case summary for list views — paginated advocate case list.
 */
@Getter
@Builder
public class CaseSummaryResponse {

    private String uuid;
    private String caseNumber;
    private String caseTitle;
    private String caseType;
    private CaseStatus status;
    private CasePriority priority;
    private String petitionerName;
    private String respondentName;
    private String courtName;
    private LocalDate filingDate;
    private LocalDate hearingDate;
    private LocalDateTime createdAt;
}
