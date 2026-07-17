package com.courtai.reports.service;

import com.courtai.reports.dto.response.CaseReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.PriorityAnalyticsResponse;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Judicial Priority Index (JPI) analytics service.
 */
public interface PriorityAnalyticsService {

    PriorityAnalyticsResponse getPriorityReport();

    List<GraphDataPoint> getPriorityDistribution();

    List<GraphDataPoint> getPriorityByCategory();

    List<GraphDataPoint> getPriorityByCourt();

    List<GraphDataPoint> getPriorityByJudge();

    Page<CaseReportResponse> getHighPriorityCases(Pageable pageable);

    List<TimeSeriesDataPoint> getPriorityScoreTrend(int year);
}
