package com.courtai.reports.service;

import com.courtai.reports.dto.response.DelayAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;

import java.util.List;

/**
 * Delay analytics service.
 */
public interface DelayAnalyticsService {

    DelayAnalyticsResponse getDelayReport();

    List<GraphDataPoint> getDelayDistribution();

    List<GraphDataPoint> getDelayByCourt();

    List<GraphDataPoint> getDelayByJudge();

    List<GraphDataPoint> getDelayByType();

    List<GraphDataPoint> getAdjournmentReasons();
}
