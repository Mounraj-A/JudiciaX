package com.courtai.ai.workflow.validator.impl;

import com.courtai.ai.workflow.dto.WorkflowRequest;
import com.courtai.ai.workflow.exception.WorkflowValidationException;
import com.courtai.ai.workflow.model.PipelineStage;
import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.validator.WorkflowValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WorkflowValidationServiceImpl implements WorkflowValidationService {

    @Override
    public void validateRequest(WorkflowRequest request) {
        if (request == null) {
            throw new WorkflowValidationException("Workflow request cannot be null");
        }
        if (!StringUtils.hasText(request.getCaseUuid())) {
            throw new WorkflowValidationException("Case UUID is required to start a workflow");
        }
        if (request.getWorkflowType() == null) {
            throw new WorkflowValidationException("Workflow Type must be specified");
        }
    }

    @Override
    public void validatePipeline(Workflow workflow, List<PipelineStage> stages) {
        if (workflow == null) {
            throw new WorkflowValidationException("Workflow entity cannot be null");
        }
        if (stages == null || stages.isEmpty()) {
            throw new WorkflowValidationException("Pipeline must have at least one stage to execute");
        }
        
        // Ensure COMPLETED is the final stage if it exists, or dynamically append it in the builder.
        // As a validation step, we just ensure it's not empty.
    }
}
