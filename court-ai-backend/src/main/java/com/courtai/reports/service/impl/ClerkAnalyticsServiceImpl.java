package com.courtai.reports.service.impl;

import com.courtai.clerk.entity.Clerk;
import com.courtai.clerk.repository.ClerkRepository;
import com.courtai.reports.dto.response.ClerkReportResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import com.courtai.reports.mapper.ReportMapper;
import com.courtai.reports.service.ClerkAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of ClerkAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClerkAnalyticsServiceImpl implements ClerkAnalyticsService {

    private final ClerkRepository clerkRepository;
    private final ReportMapper reportMapper;

    @Override
    public ClerkReportResponse getClerkReport(String clerkUuid) {
        Clerk clerk = clerkRepository.findByUuidAndIsDeletedFalse(clerkUuid)
                .orElseThrow(() -> new RuntimeException("Clerk not found"));
        return buildClerkReport(clerk);
    }

    @Override
    public ClerkReportResponse getClerkReportByPrincipal(String principalUsername) {
        Clerk clerk = clerkRepository.findByUserUsernameAndIsDeletedFalse(principalUsername)
                .orElseThrow(() -> new RuntimeException("Clerk profile not found for current user"));
        return buildClerkReport(clerk);
    }

    @Override
    public Page<ClerkReportResponse> getAllClerkReports(Pageable pageable) {
        return clerkRepository.findAll(pageable).map(this::buildClerkReport);
    }

    @Override
    public List<GraphDataPoint> getDuplicateDetectionByCourt() {
        return List.of();
    }

    @Override
    public List<TimeSeriesDataPoint> getMonthlyRegistrationTrend(String clerkUuid, int year) {
        return List.of();
    }

    private ClerkReportResponse buildClerkReport(Clerk clerk) {
        ClerkReportResponse response = reportMapper.toClerkReport(clerk);
        response.setRegisteredCases(0); // Mocked fields for now, fetch via repos
        return response;
    }
}
