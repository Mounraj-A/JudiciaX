package com.courtai.reports.service.impl;

import com.courtai.reports.dto.response.PerformanceReportResponse;
import com.courtai.reports.dto.response.ResearchAnalyticsResponse;
import com.courtai.reports.service.PerformanceAnalyticsService;
import com.courtai.reports.service.ReportGenerationService;
import com.courtai.reports.service.ResearchAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ReportGenerationService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportGenerationServiceImpl implements ReportGenerationService {

    private final PerformanceAnalyticsService performanceAnalyticsService;
    private final ResearchAnalyticsService researchAnalyticsService;

    @Override
    public PerformanceReportResponse generateCourtPerformanceReport(int year) {
        return performanceAnalyticsService.getAnnualReport(year);
    }

    @Override
    public PerformanceReportResponse generateMonthlyReport(int year, int month) {
        return performanceAnalyticsService.getMonthlyReport(year, month);
    }

    @Override
    public PerformanceReportResponse generateQuarterlyReport(int year, int quarter) {
        return performanceAnalyticsService.getQuarterlyReport(year, quarter);
    }

    @Override
    public PerformanceReportResponse generateAnnualReport(int year) {
        return performanceAnalyticsService.getAnnualReport(year);
    }

    @Override
    public ResearchAnalyticsResponse generateResearchEvaluationReport() {
        return researchAnalyticsService.getResearchSummary();
    }
}
