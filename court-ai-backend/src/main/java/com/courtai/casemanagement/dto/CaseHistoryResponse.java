package com.courtai.casemanagement.dto;

import com.courtai.common.enums.CaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Single entry in the case status history audit trail. */
@Getter
@Builder
public class CaseHistoryResponse {
    private String uuid;
    private CaseStatus fromStatus;
    private String fromStatusLabel;
    private CaseStatus toStatus;
    private String toStatusLabel;
    private String changedByUuid;
    private String changedByRole;
    private String remarks;
    private LocalDateTime changedAt;
}
