package com.courtai.clerk.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** Response returned after a case is officially registered by the clerk. */
@Data
@Builder
public class CaseRegistrationResponse {
    private String uuid;
    private String caseNumber;
    private String officialCaseNumber;
    private String caseTitle;
    private String status;
    private LocalDateTime registeredAt;
    private String registeredByClerkName;
    private Integer judgeQueuePosition;
    private String message;
}
