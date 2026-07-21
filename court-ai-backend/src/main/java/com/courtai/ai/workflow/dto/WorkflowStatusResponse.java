package com.courtai.ai.workflow.dto;

import com.courtai.ai.workflow.state.WorkflowState;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class WorkflowStatusResponse {
    private String workflowUuid;
    private WorkflowState state;
    private String currentStage;
    private LocalDateTime startedTime;
    private LocalDateTime completedTime;
    private int retryCount;
}
