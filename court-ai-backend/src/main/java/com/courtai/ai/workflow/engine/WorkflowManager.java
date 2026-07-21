package com.courtai.ai.workflow.engine;

import com.courtai.ai.workflow.dto.WorkflowRequest;
import com.courtai.ai.workflow.model.Workflow;

public interface WorkflowManager {
    
    Workflow startWorkflow(WorkflowRequest request, String createdBy);
    
    Workflow pauseWorkflow(String workflowUuid);
    
    Workflow resumeWorkflow(String workflowUuid);
    
    Workflow cancelWorkflow(String workflowUuid);
    
    Workflow restartWorkflow(String workflowUuid);
    
    Workflow retryWorkflow(String workflowUuid);
    
    Workflow archiveWorkflow(String workflowUuid);
    
    Workflow getWorkflow(String workflowUuid);
}
