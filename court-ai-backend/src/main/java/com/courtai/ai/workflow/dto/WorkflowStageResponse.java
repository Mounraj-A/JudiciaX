package com.courtai.ai.workflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowStageResponse {
    private String stage;
    private String status;
    private long executionTimeMs;
}
