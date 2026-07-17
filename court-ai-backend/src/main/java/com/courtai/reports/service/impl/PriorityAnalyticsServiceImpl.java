package com.courtai.reports.service.impl;

import com.courtai.reports.dto.response.CaseReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.PriorityAnalyticsResponse;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.mapper.AnalyticsMapper;
import com.courtai.reports.repository.ReportQueryRepository;
import com.courtai.reports.service.PriorityAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of PriorityAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriorityAnalyticsServiceImpl implements PriorityAnalyticsService {

    private final ReportQueryRepository reportQueryRepository;
    private final AnalyticsMapper analyticsMapper;

    @Override
    public PriorityAnalyticsResponse getPriorityReport() {
        return PriorityAnalyticsResponse.builder()
                .avgPriorityScore(0.0)
                .priorityDistribution(getPriorityDistribution())
                .priorityByCategory(getPriorityByCategory())
                .build();
    }

    @Override
    public List<GraphDataPoint> getPriorityDistribution() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.countGroupedByPriority());
    }

    @Override
    public List<GraphDataPoint> getPriorityByCategory() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.avgPriorityScoreGroupedByCategory());
    }

    @Override
    public List<GraphDataPoint> getPriorityByCourt() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.avgPriorityScoreGroupedByCourt());
    }

    @Override
    public List<GraphDataPoint> getPriorityByJudge() {
        return analyticsMapper.toGraphDataPoints(reportQueryRepository.avgPriorityScoreGroupedByJudge());
    }

    @Override
    public Page<CaseReportResponse> getHighPriorityCases(Pageable pageable) {
        return Page.empty(); // Mocked
    }

    @Override
    public List<TimeSeriesDataPoint> getPriorityScoreTrend(int year) {
        return List.of();
    }
}
