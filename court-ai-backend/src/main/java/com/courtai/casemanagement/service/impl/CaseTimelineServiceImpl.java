package com.courtai.casemanagement.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casemanagement.dto.CaseTimelineResponse;
import com.courtai.casemanagement.entity.CaseTimeline;
import com.courtai.casemanagement.mapper.TimelineMapper;
import com.courtai.casemanagement.repository.CaseTimelineRepository;
import com.courtai.casemanagement.service.CaseTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseTimelineServiceImpl implements CaseTimelineService {

    private final CaseTimelineRepository timelineRepository;
    private final TimelineMapper         timelineMapper;

    @Override
    @Transactional
    public void recordEvent(CaseFile caseFile, String eventType, String eventLabel,
                            String actorUuid, String actorRole, String actorName, String reason) {
        CaseTimeline event = CaseTimeline.builder()
                .caseFile(caseFile)
                .eventType(eventType)
                .eventLabel(eventLabel != null ? eventLabel : eventType)
                .actorUuid(actorUuid)
                .actorRole(actorRole)
                .actorName(actorName)
                .reason(reason)
                .eventTime(LocalDateTime.now())
                .build();
        timelineRepository.save(event);
    }

    @Override
    public List<CaseTimelineResponse> getTimeline(String caseUuid) {
        return timelineMapper.toResponseList(
                timelineRepository.findByCaseUuidOrderByEventTimeAsc(caseUuid));
    }
}
