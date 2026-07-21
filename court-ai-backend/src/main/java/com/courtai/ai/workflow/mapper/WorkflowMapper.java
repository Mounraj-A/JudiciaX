package com.courtai.ai.workflow.mapper;

import com.courtai.ai.workflow.dto.WorkflowStatusResponse;
import com.courtai.ai.workflow.dto.WorkflowSummaryResponse;
import com.courtai.ai.workflow.model.Workflow;
import org.springframework.stereotype.Component;

@Component("aiWorkflowMapper")
public class WorkflowMapper {

    public WorkflowStatusResponse toStatusResponse(Workflow workflow) {
        if (workflow == null) return null;
        
        return WorkflowStatusResponse.builder()
                .workflowUuid(workflow.getWorkflowUuid())
                .state(workflow.getWorkflowState())
                .currentStage(workflow.getCurrentStage() != null ? workflow.getCurrentStage().name() : null)
                .startedTime(workflow.getStartedTime())
                .completedTime(workflow.getCompletedTime())
                .retryCount(workflow.getRetryCount())
                .build();
    }

    public WorkflowSummaryResponse toSummaryResponse(Workflow workflow) {
        if (workflow == null) return null;

        return WorkflowSummaryResponse.builder()
                .workflowUuid(workflow.getWorkflowUuid())
                .caseUuid(workflow.getCaseUuid())
                .type(workflow.getWorkflowType())
                .state(workflow.getWorkflowState())
                .currentStage(workflow.getCurrentStage() != null ? workflow.getCurrentStage().name() : null)
                .build();
    }
}
