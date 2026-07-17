package com.courtai.reports.service.impl;

import com.courtai.reports.dto.response.DelayAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.mapper.AnalyticsMapper;
import com.courtai.reports.repository.ReportQueryRepository;
import com.courtai.reports.service.DelayAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of DelayAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DelayAnalyticsServiceImpl implements DelayAnalyticsService {

    private final ReportQueryRepository reportQueryRepository;
    private final AnalyticsMapper analyticsMapper;

    @Override
    public DelayAnalyticsResponse getDelayReport() {
        Double avgAge = reportQueryRepository.avgAgeInDaysForPendingCases();
        return DelayAnalyticsResponse.builder()
                .avgDelayDays(avgAge != null ? Math.round(avgAge * 100.0) / 100.0 : 0.0)
                .delayDistribution(getDelayDistribution())
                .build();
    }

    @Override
    public List<GraphDataPoint> getDelayDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.caseAgeDistribution());
    }

    @Override
    public List<GraphDataPoint> getDelayByCourt() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getDelayByJudge() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getDelayByType() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getAdjournmentReasons() {
        return List.of(); // Aggregate from Hearing.adjournReason
    }
}
