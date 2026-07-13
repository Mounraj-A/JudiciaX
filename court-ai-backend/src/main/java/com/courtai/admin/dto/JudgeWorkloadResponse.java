package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

/** Judge workload statistics for the admin judge administration panel. */
@Getter
@Builder
public class JudgeWorkloadResponse {
    private String judgeUuid;
    private String judgeIdNumber;
    private String judgeName;
    private String designation;
    private String courtName;
    private String specialization;
    private long totalAssignedCases;
    private long activeCases;
    private long pendingHearings;
    private long disposedCases;
    private long reservedJudgments;
}
