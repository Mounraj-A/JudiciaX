package com.courtai.reports.service;

import com.courtai.reports.dto.response.PerformanceReportResponse;

/**
 * Performance analytics service — aggregated system performance for a period.
 */
public interface PerformanceAnalyticsService {

    PerformanceReportResponse getMonthlyReport(int year, int month);

    PerformanceReportResponse getQuarterlyReport(int year, int quarter);

    PerformanceReportResponse getAnnualReport(int year);
}
