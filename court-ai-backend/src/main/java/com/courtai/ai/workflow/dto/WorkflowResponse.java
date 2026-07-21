package com.courtai.ai.workflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowResponse {
    private String workflowUuid;
    private String message;
    private String status;
}
