package com.courtai.casemanagement.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Single entry in the ordered case timeline. */
@Getter
@Builder
public class CaseTimelineResponse {
    private String uuid;
    private String eventType;
    private String eventLabel;
    private String description;
    private String actorUuid;
    private String actorRole;
    private String actorName;
    private String reason;
    private LocalDateTime eventTime;
}
