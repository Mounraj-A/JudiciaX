package com.courtai.clerk.dto;

import com.courtai.common.enums.CaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** One entry in a case's status change timeline. */
@Data
@Builder
public class CaseTimelineResponse {
    private String uuid;
    private CaseStatus fromStatus;
    private CaseStatus toStatus;
    private LocalDateTime changedAt;
    private String changedByUuid;
    private String changedByRole;
    private String remarks;
}
