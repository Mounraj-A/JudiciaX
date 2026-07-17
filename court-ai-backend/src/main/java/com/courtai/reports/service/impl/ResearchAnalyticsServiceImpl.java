package com.courtai.reports.service.impl;

import com.courtai.ai.entity.CaseAnalysis;
import com.courtai.ai.repository.CaseAnalysisRepository;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.document.repository.DocumentRepository;
import com.courtai.evidence.repository.EvidenceRepository;
import com.courtai.hearing.repository.HearingRepository;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.ResearchAnalyticsResponse;
import com.courtai.reports.dto.response.ResearchDatasetRow;
import com.courtai.reports.mapper.ResearchMapper;
import com.courtai.reports.repository.ReportQueryRepository;
import com.courtai.reports.service.ResearchAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ResearchAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResearchAnalyticsServiceImpl implements ResearchAnalyticsService {

    private final CaseFileRepository caseFileRepository;
    private final CaseAnalysisRepository caseAnalysisRepository;
    private final DocumentRepository documentRepository;
    private final EvidenceRepository evidenceRepository;
    private final HearingRepository hearingRepository;
    private final ReportQueryRepository reportQueryRepository;
    private final ResearchMapper researchMapper;

    @Override
    public Page<ResearchDatasetRow> getResearchDataset(Pageable pageable) {
        // Find all non-deleted cases with pagination
        Page<CaseFile> cases = caseFileRepository.findAll(pageable);
        List<ResearchDatasetRow> rows = cases.stream()
                .map(this::buildDatasetRow)
                .toList();
        return new PageImpl<>(rows, pageable, cases.getTotalElements());
    }

    @Override
    public List<ResearchDatasetRow> generateFullDataset() {
        // Use the lightweight projection if possible for large exports,
        // but here we fall back to finding all for complete mapping.
        List<CaseFile> cases = caseFileRepository.findAll();
        List<ResearchDatasetRow> rows = new ArrayList<>();
        for (CaseFile c : cases) {
            rows.add(buildDatasetRow(c));
        }
        return rows;
    }

    private ResearchDatasetRow buildDatasetRow(CaseFile caseFile) {
        CaseAnalysis analysis = caseAnalysisRepository.findTopByCaseFileIdOrderByCreatedAtDesc(caseFile.getId())
                .orElse(null);
        
        long totalDocs = documentRepository.countByCaseFileId(caseFile.getId());
        long verifiedDocs = documentRepository.countByCaseFileIdAndIsVerifiedTrue(caseFile.getId());
        
        long totalEvidence = evidenceRepository.countByCaseFileId(caseFile.getId());
        long verifiedEvidence = evidenceRepository.countByCaseFileIdAndIsVerifiedTrue(caseFile.getId());
        
        long hearingCount = hearingRepository.countByCaseFileId(caseFile.getId());
        long adjournCount = 0; // Requires explicit query for adjourned hearings
        
        return researchMapper.toDatasetRow(
                caseFile, analysis, totalDocs, verifiedDocs,
                totalEvidence, verifiedEvidence, hearingCount, adjournCount
        );
    }

    @Override
    public ResearchAnalyticsResponse getResearchSummary() {
        return ResearchAnalyticsResponse.builder()
                .totalDatasetRows(caseFileRepository.count())
                .build();
    }

    @Override
    public ResearchAnalyticsResponse getExplainabilityReport() {
        return ResearchAnalyticsResponse.builder().build();
    }

    @Override
    public List<GraphDataPoint> getPriorityAccuracyReport() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getTrustAnalysisBreakdown() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getFeatureCorrelationSummary() {
        return List.of();
    }
}
