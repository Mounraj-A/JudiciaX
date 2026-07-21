package com.courtai.ai.workflow.service;

import com.courtai.ai.workflow.dto.ExecutionHistoryResponse;
import com.courtai.ai.workflow.dto.WorkflowRequest;
import com.courtai.ai.workflow.dto.WorkflowResponse;
import com.courtai.ai.workflow.dto.WorkflowStatusResponse;
import com.courtai.ai.workflow.dto.WorkflowSummaryResponse;

import java.util.List;

public interface WorkflowService {
    
    WorkflowResponse startWorkflow(WorkflowRequest request, String createdBy);
    
    WorkflowResponse retryWorkflow(String workflowUuid);
    
    WorkflowResponse cancelWorkflow(String workflowUuid);
    
    WorkflowResponse restartWorkflow(String workflowUuid);
    
    WorkflowStatusResponse getWorkflowStatus(String workflowUuid);
    
    ExecutionHistoryResponse getWorkflowHistory(String workflowUuid);
    
    List<WorkflowSummaryResponse> getAllWorkflows();
}
