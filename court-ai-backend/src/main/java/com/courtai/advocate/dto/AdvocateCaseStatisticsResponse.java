package com.courtai.advocate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvocateCaseStatisticsResponse {
    private long totalCases;
    private long activeCases;
    private long draftCases;
    private long pendingCases;
    private long upcomingHearings;
    private long disposedCases;
}
