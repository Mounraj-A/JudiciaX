package com.courtai.clerk.mapper;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.clerk.dto.CaseScrutinyResponse;
import com.courtai.clerk.dto.ClerkCaseSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.courtai.master.entity.CaseType;

/**
 * MapStruct mapper for converting {@link CaseFile} entities to clerk-facing DTOs.
 */
@Mapper(componentModel = "spring")
public interface ClerkCaseMapper {

    @Mapping(target = "uuid",                   source = "uuid")
    @Mapping(target = "caseNumber",             source = "caseNumber")
    @Mapping(target = "officialCaseNumber",      source = "officialCaseNumber")
    @Mapping(target = "caseTitle",              source = "caseTitle")
    @Mapping(target = "caseDescription",        source = "caseDescription")
    @Mapping(target = "caseType",               source = "caseType.typeCode")
    @Mapping(target = "status",                 source = "status")
    @Mapping(target = "priority",               source = "priority")
    @Mapping(target = "petitionerName",         source = "petitionerName")
    @Mapping(target = "respondentName",         source = "respondentName")
    @Mapping(target = "policeStation",          source = "policeStation")
    @Mapping(target = "actSection",             source = "actSection")
    @Mapping(target = "filingDate",             source = "filingDate")
    @Mapping(target = "filingYear",             source = "filingYear")
    @Mapping(target = "scrutinyClerkUuid",      source = "scrutinyClerkUuid")
    @Mapping(target = "verificationRemarks",    source = "verificationRemarks")
    @Mapping(target = "isDuplicateChecked",     source = "isDuplicateChecked")
    @Mapping(target = "isJurisdictionVerified", source = "isJurisdictionVerified")
    @Mapping(target = "duplicateCaseUuids",     source = "duplicateCaseUuids")
    @Mapping(target = "judgeQueuePosition",     source = "judgeQueuePosition")
    @Mapping(target = "registeredAt",           source = "registeredAt")
    @Mapping(target = "registeredByUuid",       source = "registeredByUuid")
    @Mapping(target = "createdAt",              source = "createdAt")
    @Mapping(target = "updatedAt",              source = "updatedAt")
    // Court fields
    @Mapping(target = "courtUuid",              source = "court.uuid",       qualifiedByName = "nullSafe")
    @Mapping(target = "courtName",              source = "court.courtName",  qualifiedByName = "nullSafe")
    @Mapping(target = "courtCode",              source = "court.courtCode",  qualifiedByName = "nullSafe")
    @Mapping(target = "district",               source = "court.district",   qualifiedByName = "nullSafe")
    @Mapping(target = "state",                  source = "court.state",      qualifiedByName = "nullSafe")
    // Advocate fields
    @Mapping(target = "petitionerAdvocateName",      source = "petitionerAdvocate.user.fullName",           qualifiedByName = "nullSafe")
    @Mapping(target = "petitionerAdvocateBarNumber", source = "petitionerAdvocate.barCouncilNumber",        qualifiedByName = "nullSafe")
    @Mapping(target = "petitionerAdvocateUuid",      source = "petitionerAdvocate.uuid",                   qualifiedByName = "nullSafe")
    // Category
    @Mapping(target = "caseCategoryName",       source = "caseCategory.categoryName", qualifiedByName = "nullSafe")
    // Counts — populated by service after mapping
    @Mapping(target = "documentCount",          ignore = true)
    @Mapping(target = "unverifiedDocumentCount",ignore = true)
    @Mapping(target = "evidenceCount",          ignore = true)
    @Mapping(target = "unverifiedEvidenceCount",ignore = true)
    @Mapping(target = "openObjectionCount",     ignore = true)
    CaseScrutinyResponse toCaseScrutinyResponse(CaseFile caseFile);

    @Mapping(target = "uuid",                   source = "uuid")
    @Mapping(target = "caseNumber",             source = "caseNumber")
    @Mapping(target = "officialCaseNumber",      source = "officialCaseNumber")
    @Mapping(target = "caseTitle",              source = "caseTitle")
    @Mapping(target = "caseType",               source = "caseType.typeCode")
    @Mapping(target = "status",                 source = "status")
    @Mapping(target = "petitionerName",         source = "petitionerName")
    @Mapping(target = "respondentName",         source = "respondentName")
    @Mapping(target = "filingDate",             source = "filingDate")
    @Mapping(target = "isDuplicateChecked",     source = "isDuplicateChecked")
    @Mapping(target = "createdAt",              source = "createdAt")
    @Mapping(target = "courtName",              source = "court.courtName",            qualifiedByName = "nullSafe")
    @Mapping(target = "petitionerAdvocateName", source = "petitionerAdvocate.user.fullName", qualifiedByName = "nullSafe")
    @Mapping(target = "openObjectionCount",     ignore = true)
    ClerkCaseSummaryResponse toClerkCaseSummaryResponse(CaseFile caseFile);

    @Named("nullSafe")
    default String nullSafe(String value) {
        return value;
    }

    default String mapCaseType(CaseType caseType) {
        return caseType != null ? caseType.getTypeCode() : null;
    }
}
