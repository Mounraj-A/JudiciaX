package com.courtai.ai.workflow.dto;

import com.courtai.ai.workflow.model.WorkflowType;
import com.courtai.ai.workflow.state.WorkflowState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowSummaryResponse {
    private String workflowUuid;
    private String caseUuid;
    private WorkflowType type;
    private WorkflowState state;
    private String currentStage;
}
