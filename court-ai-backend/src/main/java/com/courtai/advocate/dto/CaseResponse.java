package com.courtai.advocate.dto;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Full case detail response for the Advocate Portal.
 */
@Getter
@Builder
public class CaseResponse {

    private String uuid;
    private String caseNumber;
    private String cnrNumber;
    private String caseTitle;
    private String caseDescription;
    private CaseType caseType;
    private CaseStatus status;
    private CasePriority priority;

    // Parties
    private String petitionerName;
    private String respondentName;

    // Court
    private String courtName;
    private String courtUuid;

    // Judge
    private String assignedJudgeName;
    private String assignedJudgeUuid;

    // Legal details
    private String policeStation;
    private String actSection;

    // Dates
    private LocalDate filingDate;
    private LocalDate hearingDate;
    private Integer filingYear;

    // AI scores (read-only)
    private Double urgencyScore;
    private Double trustScore;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
