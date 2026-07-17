package com.courtai.reports.service;

import com.courtai.reports.dto.response.*;

/**
 * Report generation service — assembles multi-domain composite reports.
 */
public interface ReportGenerationService {

    PerformanceReportResponse generateCourtPerformanceReport(int year);

    PerformanceReportResponse generateMonthlyReport(int year, int month);

    PerformanceReportResponse generateQuarterlyReport(int year, int quarter);

    PerformanceReportResponse generateAnnualReport(int year);

    ResearchAnalyticsResponse generateResearchEvaluationReport();
}
