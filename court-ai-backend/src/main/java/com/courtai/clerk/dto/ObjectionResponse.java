package com.courtai.clerk.dto;

import com.courtai.common.enums.ObjectionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** Clerk objection detail response. */
@Data
@Builder
public class ObjectionResponse {
    private String uuid;
    private String caseUuid;
    private String caseNumber;
    private String raisedByClerkUuid;
    private ObjectionType objectionType;
    private String reason;
    private String missingDocuments;
    private String correctionRequired;
    private Boolean isResolved;
    private LocalDateTime resolvedAt;
    private String resolvedByUuid;
    private String resolutionNotes;
    private LocalDateTime createdAt;
}
