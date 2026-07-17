package com.courtai.reports.service;

import com.courtai.reports.dto.response.DelayAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;

import java.util.List;

/**
 * Backlog analytics service — cases pending beyond thresholds.
 */
public interface BacklogAnalyticsService {

    /** Full backlog report. Backlog = pending cases older than 90 days. */
    DelayAnalyticsResponse getBacklogReport();

    List<GraphDataPoint> getBacklogByCourt();

    List<GraphDataPoint> getBacklogByType();

    List<GraphDataPoint> getBacklogByJudge();

    List<TimeSeriesDataPoint> getBacklogTrend(int year);
}
