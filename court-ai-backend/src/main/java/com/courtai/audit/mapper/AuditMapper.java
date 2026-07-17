package com.courtai.audit.mapper;

import com.courtai.audit.dto.AuditResponse;
import com.courtai.audit.dto.AuditSummaryResponse;
import com.courtai.audit.dto.AuditTimelineResponse;
import com.courtai.audit.entity.AuditEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditMapper {
    AuditResponse toResponse(AuditEvent entity);
    List<AuditResponse> toResponseList(List<AuditEvent> entities);

    AuditSummaryResponse toSummaryResponse(AuditEvent entity);
    List<AuditSummaryResponse> toSummaryResponseList(List<AuditEvent> entities);

    AuditTimelineResponse toTimelineResponse(AuditEvent entity);
    List<AuditTimelineResponse> toTimelineResponseList(List<AuditEvent> entities);
}
