package com.courtai.advocate.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CaseTimelineResponse {
    private String uuid;
    private String eventType;
    private String eventTitle;
    private String eventDescription;
    private LocalDateTime eventDate;
    private String actorName;
    private String actorRole;
}
