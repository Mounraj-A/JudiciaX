package com.courtai.reports.service.impl;

import com.courtai.reports.dto.response.DelayAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.repository.ReportQueryRepository;
import com.courtai.reports.service.BacklogAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of BacklogAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BacklogAnalyticsServiceImpl implements BacklogAnalyticsService {

    private final ReportQueryRepository reportQueryRepository;

    @Override
    public DelayAnalyticsResponse getBacklogReport() {
        return DelayAnalyticsResponse.builder()
                .backlogCount(0) // Query pending cases older than 90 days
                .build();
    }

    @Override
    public List<GraphDataPoint> getBacklogByCourt() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getBacklogByType() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getBacklogByJudge() {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getBacklogTrend(int year) {
        return List.of();
    }
}
