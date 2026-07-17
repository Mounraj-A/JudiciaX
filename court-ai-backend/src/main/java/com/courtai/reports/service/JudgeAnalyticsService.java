package com.courtai.reports.service;

import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.JudgeReportResponse;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Judge performance analytics service.
 */
public interface JudgeAnalyticsService {

    JudgeReportResponse getJudgeReport(String judgeUuid);

    /** Resolves judge UUID from JWT principal (email). */
    JudgeReportResponse getJudgeReportByPrincipal(String principalUsername);

    Page<JudgeReportResponse> getAllJudgeReports(Pageable pageable);

    List<JudgeReportResponse> getJudgesRankedByPerformance();

    List<GraphDataPoint> getDisposalByType(String judgeUuid);

    List<TimeSeriesDataPoint> getDisposalTrend(String judgeUuid, int year);

    Double calculatePerformanceScore(String judgeUuid);
}
