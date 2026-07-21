package com.courtai.casemanagement.mapper;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseFlag;
import com.courtai.casemanagement.dto.CaseDetailsResponse;
import com.courtai.casemanagement.dto.CaseSummaryResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link CaseFile} to Case Management DTOs.
 * Manual mapping is used to avoid deep lazy-loading pitfalls
 * that MapStruct code-gen can silently trigger.
 */
@Component
public class CaseMapper {

    public CaseSummaryResponse toSummary(CaseFile c) {
        return CaseSummaryResponse.builder()
                .uuid(c.getUuid())
                .caseNumber(c.getCaseNumber())
                .cnrNumber(c.getCnrNumber())
                .officialCaseNumber(c.getOfficialCaseNumber())
                .caseTitle(c.getCaseTitle())
                .caseType(c.getCaseType() != null ? c.getCaseType().getTypeCode() : null)
                .status(c.getStatus())
                .priority(c.getPriority())
                .priorityScore(c.getPriorityScore())
                .petitionerName(c.getPetitionerName())
                .respondentName(c.getRespondentName())
                .courtName(c.getCourtName())
                .assignedJudgeName(c.getAssignedJudge() != null
                        ? c.getAssignedJudge().getJudgeIdNumber() : null)
                .filingDate(c.getFilingDate())
                .hearingDate(c.getHearingDate())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    public CaseDetailsResponse toDetails(CaseFile c, CaseFlag flags) {
        CaseDetailsResponse.CaseDetailsResponseBuilder b = CaseDetailsResponse.builder()
                .uuid(c.getUuid())
                .caseNumber(c.getCaseNumber())
                .cnrNumber(c.getCnrNumber())
                .officialCaseNumber(c.getOfficialCaseNumber())
                .filingYear(c.getFilingYear())
                .caseTitle(c.getCaseTitle())
                .caseDescription(c.getCaseDescription())
                .caseType(c.getCaseType() != null ? c.getCaseType().getTypeCode() : null)
                .status(c.getStatus())
                .priority(c.getPriority())
                .priorityScore(c.getPriorityScore())
                .petitionerName(c.getPetitionerName())
                .respondentName(c.getRespondentName())
                .courtName(c.getCourtName())
                .policeStation(c.getPoliceStation())
                .actSection(c.getActSection())
                .filingDate(c.getFilingDate())
                .hearingDate(c.getHearingDate())
                .registeredAt(c.getRegisteredAt())
                .verificationRemarks(c.getVerificationRemarks())
                .isDuplicateChecked(c.getIsDuplicateChecked())
                .isJurisdictionVerified(c.getIsJurisdictionVerified())
                .judgeQueuePosition(c.getJudgeQueuePosition())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy());

        if (c.getCourt() != null) {
            b.courtUuid(c.getCourt().getUuid());
        }
        if (c.getAssignedJudge() != null) {
            b.assignedJudgeUuid(c.getAssignedJudge().getUuid())
             .assignedJudgeName(c.getAssignedJudge().getJudgeIdNumber())
             .assignedJudgeDesignation(c.getAssignedJudge().getDesignation());
        }
        if (c.getPetitionerAdvocate() != null) {
            b.petitionerAdvocateUuid(c.getPetitionerAdvocate().getUuid());
        }
        if (c.getRespondentAdvocate() != null) {
            b.respondentAdvocateUuid(c.getRespondentAdvocate().getUuid());
        }
        if (c.getCaseCategory() != null) {
            b.caseCategoryName(c.getCaseCategory().getCategoryName());
        }
        if (flags != null) {
            b.medicalEmergency(flags.getMedicalEmergency())
             .childInvolved(flags.getChildInvolved())
             .womenSafety(flags.getWomenSafety())
             .seniorCitizen(flags.getSeniorCitizen())
             .disability(flags.getDisability())
             .financialFraud(flags.getFinancialFraud())
             .cyberCrime(flags.getCyberCrime())
             .threatToLife(flags.getThreatToLife())
             .highPublicInterest(flags.getHighPublicInterest());
        }
        return b.build();
    }
}
