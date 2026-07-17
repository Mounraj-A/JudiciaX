package com.courtai.reports.service.impl;

import com.courtai.reports.dto.response.PerformanceReportResponse;
import com.courtai.reports.service.PerformanceAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of PerformanceAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceAnalyticsServiceImpl implements PerformanceAnalyticsService {

    @Override
    public PerformanceReportResponse getMonthlyReport(int year, int month) {
        return buildReport(year + "-" + month, "MONTHLY");
    }

    @Override
    public PerformanceReportResponse getQuarterlyReport(int year, int quarter) {
        return buildReport("Q" + quarter + "-" + year, "QUARTERLY");
    }

    @Override
    public PerformanceReportResponse getAnnualReport(int year) {
        return buildReport(String.valueOf(year), "ANNUAL");
    }

    private PerformanceReportResponse buildReport(String period, String type) {
        return PerformanceReportResponse.builder()
                .period(period)
                .periodType(type)
                .totalCasesFiled(0)
                .totalCasesDisposed(0)
                .disposalRate(0.0)
                .build();
    }
}
