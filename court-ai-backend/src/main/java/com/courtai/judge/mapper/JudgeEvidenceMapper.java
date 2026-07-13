package com.courtai.judge.mapper;

import com.courtai.evidence.entity.Evidence;
import com.courtai.judge.dto.JudgeEvidenceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Evidence → JudgeEvidenceResponse.
 * Read-only — no reverse mapping.
 */
@Mapper(componentModel = "spring")
public interface JudgeEvidenceMapper {

    @Mapping(target = "uuid",                source = "uuid")
    @Mapping(target = "evidenceType",        source = "evidenceType")
    @Mapping(target = "title",               source = "title")
    @Mapping(target = "description",         source = "description")
    @Mapping(target = "documentUuid",        source = "document.uuid")
    @Mapping(target = "collectedAt",         source = "collectedAt")
    @Mapping(target = "collectedBy",         source = "collectedBy")
    @Mapping(target = "location",            source = "location")
    @Mapping(target = "isAdmitted",          source = "isAdmitted")
    @Mapping(target = "admissionRemarks",    source = "admissionRemarks")
    @Mapping(target = "isVerified",          source = "isVerified")
    @Mapping(target = "verificationRemarks", source = "verificationRemarks")
    @Mapping(target = "verifiedAt",          source = "verifiedAt")
    @Mapping(target = "createdAt",           source = "createdAt")
    JudgeEvidenceResponse toResponse(Evidence evidence);
}
