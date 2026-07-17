package com.courtai.reports.service;

import com.courtai.reports.dto.response.AIAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;

import java.util.List;

/**
 * AI recommendation and explainability analytics service.
 */
public interface AIAnalyticsService {

    AIAnalyticsResponse getAIReport();

    List<GraphDataPoint> getQueueStatusBreakdown();

    List<GraphDataPoint> getConfidenceDistribution();

    List<GraphDataPoint> getUrgencyDistribution();

    List<GraphDataPoint> getModelVersionDistribution();

    List<TimeSeriesDataPoint> getAIThroughputTrend(int year);

    List<TimeSeriesDataPoint> getUrgencyScoreTrend(int year);
}
