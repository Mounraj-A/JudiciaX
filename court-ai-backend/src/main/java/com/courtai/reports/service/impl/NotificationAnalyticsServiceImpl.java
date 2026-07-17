package com.courtai.reports.service.impl;

import com.courtai.notification.repository.NotificationRepository;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.NotificationAnalyticsResponse;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.service.NotificationAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of NotificationAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationAnalyticsServiceImpl implements NotificationAnalyticsService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationAnalyticsResponse getNotificationReport() {
        return NotificationAnalyticsResponse.builder()
                .totalNotifications(notificationRepository.count())
                .build();
    }

    @Override
    public List<GraphDataPoint> getTypeDistribution() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getPriorityDistribution() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getDeliveryRateByType() {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getDeliveryTrend(int year) {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getFailureTrend(int year) {
        return List.of();
    }
}
