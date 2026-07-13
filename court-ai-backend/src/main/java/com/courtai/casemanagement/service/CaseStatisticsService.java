package com.courtai.casemanagement.service;

import com.courtai.casemanagement.dto.CaseStatisticsResponse;

/** Service for aggregating case statistics across different dimensions. */
public interface CaseStatisticsService {

    /** System-wide case statistics. */
    CaseStatisticsResponse getGlobalStatistics();

    /** Statistics filtered to a specific court. */
    CaseStatisticsResponse getStatisticsByCourt(String courtUuid);

    /** Statistics filtered to a specific judge's assigned cases. */
    CaseStatisticsResponse getStatisticsByJudge(String judgeUserUuid);

    /** Statistics filtered to cases involving a specific advocate. */
    CaseStatisticsResponse getStatisticsByAdvocate(String advocateUuid);
}
