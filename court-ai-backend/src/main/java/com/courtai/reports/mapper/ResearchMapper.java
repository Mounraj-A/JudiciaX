package com.courtai.reports.mapper;

import com.courtai.ai.entity.CaseAnalysis;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.hearing.entity.Hearing;
import com.courtai.reports.dto.response.ResearchDatasetRow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Mapper for building the research dataset rows.
 *
 * <p>Converts a {@link CaseFile} + optional {@link CaseAnalysis}
 * into a flat {@link ResearchDatasetRow} for export and research use.</p>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ResearchMapper {

    /**
     * Builds a research dataset row from a case and its AI analysis.
     *
     * @param caseFile  the judicial case (required)
     * @param analysis  the AI analysis result (may be null)
     * @param totalDocs total documents submitted for this case
     * @param verifiedDocs documents verified by clerk
     * @param totalEvidence total evidence items
     * @param verifiedEvidence verified evidence items
     * @param hearingCount total hearings scheduled
     * @param adjournCount total hearings adjourned
     */
    public ResearchDatasetRow toDatasetRow(
            CaseFile caseFile,
            CaseAnalysis analysis,
            long totalDocs,
            long verifiedDocs,
            long totalEvidence,
            long verifiedEvidence,
            long hearingCount,
            long adjournCount
    ) {
        long ageInDays = caseFile.getFilingDate() != null
                ? ChronoUnit.DAYS.between(caseFile.getFilingDate(), LocalDate.now())
                : 0L;

        Long registrationDays = null;
        if (caseFile.getFilingDate() != null && caseFile.getRegisteredAt() != null) {
            registrationDays = ChronoUnit.DAYS.between(
                    caseFile.getFilingDate(),
                    caseFile.getRegisteredAt().toLocalDate());
        }

        double docCompleteness = totalDocs > 0 ? (double) verifiedDocs / totalDocs : 0.0;
        double evidenceCompleteness = totalEvidence > 0 ? (double) verifiedEvidence / totalEvidence : 0.0;

        boolean hasDuplicate = caseFile.getDuplicateCaseUuids() != null
                && !caseFile.getDuplicateCaseUuids().isBlank();

        ResearchDatasetRow.ResearchDatasetRowBuilder builder = ResearchDatasetRow.builder()
                .caseUuid(caseFile.getUuid())
                .caseNumber(caseFile.getCaseNumber())
                .caseType(caseFile.getCaseType() != null ? caseFile.getCaseType().name() : null)
                .categoryName(caseFile.getCaseCategory() != null ? caseFile.getCaseCategory().getCategoryName() : null)
                .status(caseFile.getStatus() != null ? caseFile.getStatus().name() : null)
                .priority(caseFile.getPriority() != null ? caseFile.getPriority().name() : null)
                .priorityScore(caseFile.getPriorityScore())
                .filingYear(caseFile.getFilingYear())
                .totalDocuments(totalDocs)
                .verifiedDocuments(verifiedDocs)
                .documentCompletenessRate(Math.round(docCompleteness * 10000.0) / 10000.0)
                .totalEvidence(totalEvidence)
                .verifiedEvidence(verifiedEvidence)
                .evidenceCompletenessRate(Math.round(evidenceCompleteness * 10000.0) / 10000.0)
                .isDuplicateChecked(caseFile.getIsDuplicateChecked())
                .isJurisdictionVerified(caseFile.getIsJurisdictionVerified())
                .hasDuplicateFlag(hasDuplicate)
                .ageInDays(ageInDays)
                .registrationDays(registrationDays)
                .hearingCount(hearingCount)
                .adjournmentCount(adjournCount)
                .courtCode(caseFile.getCourt() != null ? caseFile.getCourt().getCourtCode() : null)
                .state(caseFile.getCourt() != null ? caseFile.getCourt().getState() : null)
                .district(caseFile.getCourt() != null ? caseFile.getCourt().getDistrict() : null)
                .courtType(caseFile.getCourt() != null ? caseFile.getCourt().getCourtType() : null);

        // Populate AI fields if analysis exists
        if (analysis != null) {
            builder
                    .urgencyScore(analysis.getUrgencyScore())
                    .delayImpactScore(analysis.getDelayImpactScore())
                    .trustScore(analysis.getTrustScore())
                    .confidenceScore(analysis.getConfidenceScore())
                    .aiRecommendation(analysis.getRecommendation());
        }

        return builder.build();
    }
}
