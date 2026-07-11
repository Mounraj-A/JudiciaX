package com.courtai.advocate.mapper;

import com.courtai.advocate.dto.CaseResponse;
import com.courtai.advocate.dto.CaseSummaryResponse;
import com.courtai.casefile.entity.CaseFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link CaseFile} → advocate case DTOs.
 */
@Mapper(componentModel = "spring")
public interface CaseMapper {

    @Mapping(target = "uuid",              source = "uuid")
    @Mapping(target = "caseNumber",        source = "caseNumber")
    @Mapping(target = "cnrNumber",         source = "cnrNumber")
    @Mapping(target = "caseTitle",         source = "caseTitle")
    @Mapping(target = "caseDescription",   source = "caseDescription")
    @Mapping(target = "caseType",          source = "caseType")
    @Mapping(target = "status",            source = "status")
    @Mapping(target = "priority",          source = "priority")
    @Mapping(target = "petitionerName",    source = "petitionerName")
    @Mapping(target = "respondentName",    source = "respondentName")
    @Mapping(target = "courtName",         source = "court.courtName")
    @Mapping(target = "courtUuid",         source = "court.uuid")
    @Mapping(target = "assignedJudgeName", source = "assignedJudge.user.fullName")
    @Mapping(target = "assignedJudgeUuid", source = "assignedJudge.uuid")
    @Mapping(target = "policeStation",     source = "policeStation")
    @Mapping(target = "actSection",        source = "actSection")
    @Mapping(target = "filingDate",        source = "filingDate")
    @Mapping(target = "hearingDate",       source = "hearingDate")
    @Mapping(target = "filingYear",        source = "filingYear")
    @Mapping(target = "urgencyScore",      source = "priorityScore")
    @Mapping(target = "trustScore",        ignore = true)
    @Mapping(target = "createdAt",         source = "createdAt")
    @Mapping(target = "updatedAt",         source = "updatedAt")
    CaseResponse toCaseResponse(CaseFile caseFile);

    @Mapping(target = "uuid",           source = "uuid")
    @Mapping(target = "caseNumber",     source = "caseNumber")
    @Mapping(target = "caseTitle",      source = "caseTitle")
    @Mapping(target = "caseType",       source = "caseType")
    @Mapping(target = "status",         source = "status")
    @Mapping(target = "priority",       source = "priority")
    @Mapping(target = "petitionerName", source = "petitionerName")
    @Mapping(target = "respondentName", source = "respondentName")
    @Mapping(target = "courtName",      source = "court.courtName")
    @Mapping(target = "filingDate",     source = "filingDate")
    @Mapping(target = "hearingDate",    source = "hearingDate")
    @Mapping(target = "createdAt",      source = "createdAt")
    CaseSummaryResponse toCaseSummaryResponse(CaseFile caseFile);
}
