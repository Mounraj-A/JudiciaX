package com.courtai.reports.service.impl;

import com.courtai.judge.entity.Judge;
import com.courtai.judge.repository.JudgeRepository;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.JudgeReportResponse;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.mapper.ReportMapper;
import com.courtai.reports.service.JudgeAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of JudgeAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JudgeAnalyticsServiceImpl implements JudgeAnalyticsService {

    private final JudgeRepository judgeRepository;
    private final ReportMapper reportMapper;

    @Override
    public JudgeReportResponse getJudgeReport(String judgeUuid) {
        Judge judge = judgeRepository.findByUuidAndIsDeletedFalse(judgeUuid)
                .orElseThrow(() -> new RuntimeException("Judge not found"));
        return buildJudgeReport(judge);
    }

    @Override
    public JudgeReportResponse getJudgeReportByPrincipal(String principalUsername) {
        Judge judge = judgeRepository.findByUserUsernameAndIsDeletedFalse(principalUsername)
                .orElseThrow(() -> new RuntimeException("Judge profile not found for current user"));
        return buildJudgeReport(judge);
    }

    @Override
    public Page<JudgeReportResponse> getAllJudgeReports(Pageable pageable) {
        return judgeRepository.findAll(pageable).map(this::buildJudgeReport);
    }

    @Override
    public List<JudgeReportResponse> getJudgesRankedByPerformance() {
        // Implement logic to fetch all, calculate score, and sort
        return List.of();
    }

    @Override
    public List<GraphDataPoint> getDisposalByType(String judgeUuid) {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getDisposalTrend(String judgeUuid, int year) {
        return List.of();
    }

    @Override
    public Double calculatePerformanceScore(String judgeUuid) {
        return 0.0; // Implement formula
    }

    private JudgeReportResponse buildJudgeReport(Judge judge) {
        JudgeReportResponse response = reportMapper.toJudgeReport(judge);
        response.setAssignedCases(0); // Mocked fields for now, fetch via repos
        response.setPerformanceScore(calculatePerformanceScore(judge.getUuid()));
        return response;
    }
}
