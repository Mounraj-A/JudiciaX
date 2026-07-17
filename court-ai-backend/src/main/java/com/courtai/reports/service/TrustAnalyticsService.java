package com.courtai.reports.service;

import com.courtai.reports.dto.response.CaseReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.dto.response.TrustAnalyticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Case Trust Score (CTS) analytics service.
 */
public interface TrustAnalyticsService {

    TrustAnalyticsResponse getTrustReport();

    List<GraphDataPoint> getTrustDistribution();

    Page<CaseReportResponse> getLowTrustCases(double threshold, Pageable pageable);

    List<TimeSeriesDataPoint> getTrustScoreTrend(int year);
}
