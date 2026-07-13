package com.courtai.judge.mapper;

import com.courtai.ai.entity.CaseAnalysis;
import com.courtai.judge.dto.JudgeAiAnalysisResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for CaseAnalysis → JudgeAiAnalysisResponse.
 * Read-only — no reverse mapping.
 *
 * <p>{@code explainableAiFactors} and {@code processingTimeMs} are derived from
 * the {@code rawResponse} field at the service layer for full flexibility.</p>
 */
@Mapper(componentModel = "spring")
public interface JudgeAiMapper {

    @Mapping(target = "uuid",                source = "uuid")
    @Mapping(target = "caseUuid",            source = "caseFile.uuid")
    @Mapping(target = "urgencyScore",        source = "urgencyScore")
    @Mapping(target = "delayImpactScore",    source = "delayImpactScore")
    @Mapping(target = "priorityScore",       source = "caseFile.priorityScore")
    @Mapping(target = "trustScore",          source = "trustScore")
    @Mapping(target = "confidenceScore",     source = "confidenceScore")
    @Mapping(target = "recommendation",      source = "recommendation")
    @Mapping(target = "modelVersion",        source = "modelVersion")
    @Mapping(target = "generatedAt",         source = "generatedAt")
    @Mapping(target = "explainableAiFactors", ignore = true)  // set manually in service
    @Mapping(target = "processingTimeMs",    ignore = true)   // set manually in service
    JudgeAiAnalysisResponse toResponse(CaseAnalysis analysis);
}
