package com.courtai.audit.mapper;

import com.courtai.audit.dto.AuditIntegrityResponse;
import com.courtai.audit.entity.AuditIntegrity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IntegrityMapper {
    AuditIntegrityResponse toResponse(AuditIntegrity entity);
    List<AuditIntegrityResponse> toResponseList(List<AuditIntegrity> entities);
}
