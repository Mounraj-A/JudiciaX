package com.courtai.advocate.dto;

import com.courtai.common.enums.HearingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hearing detail response for the Advocate Portal — read-only view.
 */
@Getter
@Builder
public class HearingResponse {

    private String uuid;
    private String caseUuid;
    private String caseNumber;
    private String caseTitle;

    // Court
    private String courtName;
    private String courtRoomName;

    // Judge
    private String judgeName;

    // Schedule
    private LocalDateTime scheduledAt;
    private HearingStatus status;
    private Integer hearingNumber;
    private Boolean isVirtual;

    // Outcome
    private String adjournReason;
    private LocalDate nextHearingDate;
    private String notes;

    private LocalDateTime createdAt;
}
