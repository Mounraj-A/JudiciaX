package com.courtai.reports.service;

import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.NotificationAnalyticsResponse;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;

import java.util.List;

/**
 * Notification analytics service.
 */
public interface NotificationAnalyticsService {

    NotificationAnalyticsResponse getNotificationReport();

    List<GraphDataPoint> getTypeDistribution();

    List<GraphDataPoint> getPriorityDistribution();

    List<GraphDataPoint> getDeliveryRateByType();

    List<TimeSeriesDataPoint> getDeliveryTrend(int year);

    List<TimeSeriesDataPoint> getFailureTrend(int year);
}
