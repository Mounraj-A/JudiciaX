package com.courtai.reports.service;

import com.courtai.reports.dto.response.AdvocateReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Advocate performance analytics service.
 */
public interface AdvocateAnalyticsService {

    AdvocateReportResponse getAdvocateReport(String advocateUuid);

    /** Resolves advocate UUID from JWT principal (email). */
    AdvocateReportResponse getAdvocateReportByPrincipal(String principalUsername);

    Page<AdvocateReportResponse> getAllAdvocateReports(Pageable pageable);

    List<GraphDataPoint> getCasesByStatus(String advocateUuid);

    List<TimeSeriesDataPoint> getMonthlyFilingTrend(String advocateUuid, int year);
}
