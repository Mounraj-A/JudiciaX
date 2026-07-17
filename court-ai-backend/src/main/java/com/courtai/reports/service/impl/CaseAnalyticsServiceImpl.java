package com.courtai.reports.service.impl;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import com.courtai.reports.dto.response.CaseReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.mapper.AnalyticsMapper;
import com.courtai.reports.repository.ReportQueryRepository;
import com.courtai.reports.service.CaseAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of CaseAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseAnalyticsServiceImpl implements CaseAnalyticsService {

    private final CaseFileRepository caseFileRepository;
    private final ReportQueryRepository reportQueryRepository;
    private final AnalyticsMapper analyticsMapper;

    @Override
    public CaseReportResponse getCaseReport(String caseUuid) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return buildCaseReport(caseFile);
    }

    @Override
    public Page<CaseReportResponse> getCasesByStatus(String status, Pageable pageable) {
        return caseFileRepository.findByStatusAndIsDeletedFalse(CaseStatus.valueOf(status), pageable)
                .map(this::buildCaseReport);
    }

    @Override
    public Page<CaseReportResponse> getCasesByType(String type, Pageable pageable) {
        return caseFileRepository.findByCaseTypeAndIsDeletedFalse(CaseType.valueOf(type), pageable)
                .map(this::buildCaseReport);
    }

    @Override
    public List<GraphDataPoint> getStatusDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.countGroupedByStatus());
    }

    @Override
    public List<GraphDataPoint> getTypeDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.countGroupedByCaseType());
    }

    @Override
    public List<GraphDataPoint> getPriorityDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.countGroupedByPriority());
    }

    @Override
    public List<GraphDataPoint> getAgeDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.caseAgeDistribution());
    }

    @Override
    public List<TimeSeriesDataPoint> getMonthlyFilingTrend(int year) {
        return analyticsMapper.fillMissingMonths(
                analyticsMapper.toMonthlyTimeSeries(reportQueryRepository.monthlyFilingTrend(year)), year);
    }

    @Override
    public List<TimeSeriesDataPoint> getMonthlyDisposalTrend(int year) {
        return analyticsMapper.fillMissingMonths(
                analyticsMapper.toMonthlyTimeSeries(reportQueryRepository.monthlyDisposalTrend(year)), year);
    }

    private CaseReportResponse buildCaseReport(CaseFile caseFile) {
        // Detailed manual mapping to avoid eager loading issues
        return CaseReportResponse.builder()
                .caseUuid(caseFile.getUuid())
                .caseNumber(caseFile.getCaseNumber())
                .caseType(caseFile.getCaseType() != null ? caseFile.getCaseType().name() : null)
                .status(caseFile.getStatus() != null ? caseFile.getStatus().name() : null)
                .priorityScore(caseFile.getPriorityScore())
                // Populate other necessary fields...
                .build();
    }
}
