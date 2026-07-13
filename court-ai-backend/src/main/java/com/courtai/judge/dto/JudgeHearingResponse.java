package com.courtai.judge.dto;

import com.courtai.common.enums.HearingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hearing response returned by all hearing endpoints in the judge portal.
 */
@Getter
@Builder
public class JudgeHearingResponse {

    private String uuid;
    private String caseUuid;
    private String caseNumber;
    private String caseTitle;
    private LocalDateTime scheduledAt;
    private LocalDateTime actualStartAt;
    private LocalDateTime actualEndAt;
    private HearingStatus status;
    private String adjournReason;
    private LocalDate nextHearingDate;
    private Integer hearingNumber;
    private String notes;
    private Boolean isVirtual;
    private String courtRoomUuid;
    private String courtRoomNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
