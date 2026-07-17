package com.courtai.audit.mapper;

import com.courtai.audit.dto.SecurityAuditResponse;
import com.courtai.audit.entity.SecurityAudit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SecurityAuditMapper {
    SecurityAuditResponse toResponse(SecurityAudit entity);
    List<SecurityAuditResponse> toResponseList(List<SecurityAudit> entities);
}
