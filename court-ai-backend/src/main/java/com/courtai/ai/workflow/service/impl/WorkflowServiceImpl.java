package com.courtai.ai.workflow.service.impl;

import com.courtai.ai.workflow.dto.ExecutionHistoryResponse;
import com.courtai.ai.workflow.dto.WorkflowRequest;
import com.courtai.ai.workflow.dto.WorkflowResponse;
import com.courtai.ai.workflow.dto.WorkflowStatusResponse;
import com.courtai.ai.workflow.dto.WorkflowSummaryResponse;
import com.courtai.ai.workflow.engine.WorkflowManager;
import com.courtai.ai.workflow.mapper.WorkflowMapper;
import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowManager workflowManager;
    private final WorkflowMapper workflowMapper;

    public WorkflowServiceImpl(WorkflowManager workflowManager, @Qualifier("aiWorkflowMapper") WorkflowMapper workflowMapper) {
        this.workflowManager = workflowManager;
        this.workflowMapper = workflowMapper;
    }

    @Override
    public WorkflowResponse startWorkflow(WorkflowRequest request, String createdBy) {
        Workflow workflow = workflowManager.startWorkflow(request, createdBy);
        return WorkflowResponse.builder()
                .workflowUuid(workflow.getWorkflowUuid())
                .message("Workflow started successfully")
                .status(workflow.getWorkflowState().name())
                .build();
    }

    @Override
    public WorkflowResponse retryWorkflow(String workflowUuid) {
        Workflow workflow = workflowManager.retryWorkflow(workflowUuid);
        return WorkflowResponse.builder()
                .workflowUuid(workflow.getWorkflowUuid())
                .message("Workflow retried")
                .status(workflow.getWorkflowState().name())
                .build();
    }

    @Override
    public WorkflowResponse cancelWorkflow(String workflowUuid) {
        Workflow workflow = workflowManager.cancelWorkflow(workflowUuid);
        return WorkflowResponse.builder()
                .workflowUuid(workflow.getWorkflowUuid())
                .message("Workflow cancelled")
                .status(workflow.getWorkflowState().name())
                .build();
    }

    @Override
    public WorkflowResponse restartWorkflow(String workflowUuid) {
        Workflow workflow = workflowManager.restartWorkflow(workflowUuid);
        return WorkflowResponse.builder()
                .workflowUuid(workflow.getWorkflowUuid())
                .message("Workflow restarted")
                .status(workflow.getWorkflowState().name())
                .build();
    }

    @Override
    public WorkflowStatusResponse getWorkflowStatus(String workflowUuid) {
        return workflowMapper.toStatusResponse(workflowManager.getWorkflow(workflowUuid));
    }

    @Override
    public ExecutionHistoryResponse getWorkflowHistory(String workflowUuid) {
        Workflow workflow = workflowManager.getWorkflow(workflowUuid);
        return ExecutionHistoryResponse.builder()
                .workflowUuid(workflow.getWorkflowUuid())
                .history(workflow.getExecutionHistory())
                .build();
    }

    @Override
    public List<WorkflowSummaryResponse> getAllWorkflows() {
        // Note: For Phase 2 (memory only), we don't expose all workflows natively without adding it to the manager.
        // For compliance with the interface, return empty list or add a getter in WorkflowManager.
        return Collections.emptyList();
    }
}
