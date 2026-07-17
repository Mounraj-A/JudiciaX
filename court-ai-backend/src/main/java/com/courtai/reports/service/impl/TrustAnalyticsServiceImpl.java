package com.courtai.reports.service.impl;

import com.courtai.reports.dto.response.CaseReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.dto.response.TrustAnalyticsResponse;
import com.courtai.reports.repository.ReportQueryRepository;
import com.courtai.reports.service.TrustAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of TrustAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrustAnalyticsServiceImpl implements TrustAnalyticsService {

    private final ReportQueryRepository reportQueryRepository;

    @Override
    public TrustAnalyticsResponse getTrustReport() {
        return TrustAnalyticsResponse.builder()
                .avgTrustScore(0.0)
                .build();
    }

    @Override
    public List<GraphDataPoint> getTrustDistribution() {
        return List.of();
    }

    @Override
    public Page<CaseReportResponse> getLowTrustCases(double threshold, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public List<TimeSeriesDataPoint> getTrustScoreTrend(int year) {
        return List.of();
    }
}
