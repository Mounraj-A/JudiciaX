package com.courtai.audit.service;

import com.courtai.audit.dto.AuditResponse;
import com.courtai.audit.dto.AuditSearchRequest;
import com.courtai.audit.dto.AuditTimelineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditSearchService {
    Page<AuditResponse> search(AuditSearchRequest request, Pageable pageable);
    AuditResponse getDetails(String uuid);
    List<AuditTimelineResponse> getTimeline(String correlationId);
}
