package com.courtai.ai.workflow.validator;

import com.courtai.ai.workflow.dto.WorkflowRequest;
import com.courtai.ai.workflow.model.PipelineStage;
import com.courtai.ai.workflow.model.Workflow;

import java.util.List;

public interface WorkflowValidationService {
    
    /**
     * Validates an incoming request to start a workflow.
     */
    void validateRequest(WorkflowRequest request);
    
    /**
     * Validates a workflow and its associated generated pipeline before execution.
     */
    void validatePipeline(Workflow workflow, List<PipelineStage> stages);
}
