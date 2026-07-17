package com.courtai.reports.service.impl;

import com.courtai.ai.repository.CaseAnalysisRepository;
import com.courtai.reports.dto.response.AIAnalyticsResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.service.AIAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of AIAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AIAnalyticsServiceImpl implements AIAnalyticsService {

    private final CaseAnalysisRepository caseAnalysisRepository;

    @Override
    public AIAnalyticsResponse getAIReport() {
        return AIAnalyticsResponse.builder()
                .totalCasesAnalysed(caseAnalysisRepository.count())
                .avgConfidenceScore(0.0)
                .build();
    }

    @Override
    public List<GraphDataPoint> getQueueStatusBreakdown() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getConfidenceDistribution() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getUrgencyDistribution() {
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getModelVersionDistribution() {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getAIThroughputTrend(int year) {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getUrgencyScoreTrend(int year) {
        return List.of();
    }
}
