package com.courtai.judge.mapper;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.judge.dto.JudgeCaseResponse;
import com.courtai.judge.dto.JudgeCaseSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting CaseFile → judge case DTOs.
 * Read-only mapping — no reverse mapping needed.
 */
@Mapper(componentModel = "spring")
public interface JudgeCaseMapper {

    @Mapping(target = "uuid",               source = "uuid")
    @Mapping(target = "caseNumber",         source = "caseNumber")
    @Mapping(target = "officialCaseNumber", source = "officialCaseNumber")
    @Mapping(target = "cnrNumber",          source = "cnrNumber")
    @Mapping(target = "caseTitle",          source = "caseTitle")
    @Mapping(target = "caseType",           source = "caseType")
    @Mapping(target = "status",             source = "status")
    @Mapping(target = "priority",           source = "priority")
    @Mapping(target = "priorityScore",      source = "priorityScore")
    @Mapping(target = "petitionerName",     source = "petitionerName")
    @Mapping(target = "respondentName",     source = "respondentName")
    @Mapping(target = "filingDate",         source = "filingDate")
    @Mapping(target = "hearingDate",        source = "hearingDate")
    @Mapping(target = "createdAt",          source = "createdAt")
    JudgeCaseSummaryResponse toSummary(CaseFile caseFile);

    @Mapping(target = "uuid",                source = "uuid")
    @Mapping(target = "caseNumber",          source = "caseNumber")
    @Mapping(target = "officialCaseNumber",  source = "officialCaseNumber")
    @Mapping(target = "cnrNumber",           source = "cnrNumber")
    @Mapping(target = "filingYear",          source = "filingYear")
    @Mapping(target = "caseTitle",           source = "caseTitle")
    @Mapping(target = "caseDescription",     source = "caseDescription")
    @Mapping(target = "caseType",            source = "caseType")
    @Mapping(target = "status",              source = "status")
    @Mapping(target = "priority",            source = "priority")
    @Mapping(target = "priorityScore",       source = "priorityScore")
    @Mapping(target = "filingDate",          source = "filingDate")
    @Mapping(target = "hearingDate",         source = "hearingDate")
    @Mapping(target = "registeredAt",        source = "registeredAt")
    @Mapping(target = "petitionerName",      source = "petitionerName")
    @Mapping(target = "respondentName",      source = "respondentName")
    @Mapping(target = "courtName",           source = "courtName")
    @Mapping(target = "policeStation",       source = "policeStation")
    @Mapping(target = "actSection",          source = "actSection")
    @Mapping(target = "verificationRemarks", source = "verificationRemarks")
    @Mapping(target = "createdAt",           source = "createdAt")
    @Mapping(target = "updatedAt",           source = "updatedAt")
    JudgeCaseResponse toDetail(CaseFile caseFile);
}
