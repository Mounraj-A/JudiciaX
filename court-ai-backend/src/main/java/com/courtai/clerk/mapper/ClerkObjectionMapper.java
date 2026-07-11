package com.courtai.clerk.mapper;

import com.courtai.clerk.dto.ObjectionResponse;
import com.courtai.clerk.entity.CaseObjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper for CaseObjection → ObjectionResponse. */
@Mapper(componentModel = "spring")
public interface ClerkObjectionMapper {

    @Mapping(target = "uuid",                source = "uuid")
    @Mapping(target = "caseUuid",            source = "caseFile.uuid")
    @Mapping(target = "caseNumber",          source = "caseFile.caseNumber")
    @Mapping(target = "raisedByClerkUuid",   source = "raisedByClerkUuid")
    @Mapping(target = "objectionType",       source = "objectionType")
    @Mapping(target = "reason",              source = "reason")
    @Mapping(target = "missingDocuments",    source = "missingDocuments")
    @Mapping(target = "correctionRequired",  source = "correctionRequired")
    @Mapping(target = "isResolved",          source = "isResolved")
    @Mapping(target = "resolvedAt",          source = "resolvedAt")
    @Mapping(target = "resolvedByUuid",      source = "resolvedByUuid")
    @Mapping(target = "resolutionNotes",     source = "resolutionNotes")
    @Mapping(target = "createdAt",           source = "createdAt")
    ObjectionResponse toObjectionResponse(CaseObjection objection);
}
