package com.courtai.audit.mapper;

import com.courtai.audit.dto.ComplianceAuditResponse;
import com.courtai.audit.entity.ComplianceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ComplianceAuditMapper {
    ComplianceAuditResponse toResponse(ComplianceAudit entity);
    List<ComplianceAuditResponse> toResponseList(List<ComplianceAudit> entities);
}
