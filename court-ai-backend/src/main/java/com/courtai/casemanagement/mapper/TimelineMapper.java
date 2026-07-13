package com.courtai.casemanagement.mapper;

import com.courtai.casemanagement.dto.CaseTimelineResponse;
import com.courtai.casemanagement.entity.CaseTimeline;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/** Maps {@link CaseTimeline} entities to response DTOs. */
@Component
public class TimelineMapper {

    public CaseTimelineResponse toResponse(CaseTimeline t) {
        return CaseTimelineResponse.builder()
                .uuid(t.getUuid())
                .eventType(t.getEventType())
                .eventLabel(t.getEventLabel())
                .description(t.getDescription())
                .actorUuid(t.getActorUuid())
                .actorRole(t.getActorRole())
                .actorName(t.getActorName())
                .reason(t.getReason())
                .eventTime(t.getEventTime())
                .build();
    }

    public List<CaseTimelineResponse> toResponseList(List<CaseTimeline> timelines) {
        return timelines.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
