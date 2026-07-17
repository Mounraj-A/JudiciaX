package com.courtai.reports.service;

import com.courtai.reports.dto.response.AuditAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;

import java.util.List;

/**
 * Audit system analytics service.
 */
public interface AuditAnalyticsService {

    AuditAnalyticsResponse getAuditReport();

    List<GraphDataPoint> getActionDistribution();

    List<GraphDataPoint> getRoleDistribution();

    List<GraphDataPoint> getModuleDistribution();

    List<GraphDataPoint> getOutcomeDistribution();

    List<TimeSeriesDataPoint> getAuditGrowthTrend(int year);

    List<TimeSeriesDataPoint> getSecurityEventTrend(int year);
}
