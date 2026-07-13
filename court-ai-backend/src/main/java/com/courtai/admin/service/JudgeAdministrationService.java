package com.courtai.admin.service;

import com.courtai.admin.dto.JudgeWorkloadResponse;
import com.courtai.admin.dto.AssignJudgeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Admin service for judge administration — assignment, workload, statistics. */
public interface JudgeAdministrationService {
    Page<JudgeWorkloadResponse> getJudgeWorkloads(Pageable pageable);
    JudgeWorkloadResponse getJudgeWorkload(String judgeUserUuid);
    void assignJudgeToCase(AssignJudgeRequest request, String adminUuid);
    void transferJudge(String caseUuid, String newJudgeUserUuid, String reason, String adminUuid);
}
