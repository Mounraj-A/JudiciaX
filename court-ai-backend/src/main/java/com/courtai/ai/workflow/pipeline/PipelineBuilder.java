package com.courtai.ai.workflow.pipeline;

import com.courtai.ai.workflow.model.PipelineStage;
import com.courtai.ai.workflow.model.WorkflowType;

import java.util.List;

public interface PipelineBuilder {
    
    /**
     * Builds the ordered list of pipeline stages required for the given workflow type.
     */
    List<PipelineStage> buildPipeline(WorkflowType workflowType);
}
