package com.courtai.ai.workflow.pipeline.impl;

import com.courtai.ai.workflow.exception.WorkflowValidationException;
import com.courtai.ai.workflow.model.PipelineStage;
import com.courtai.ai.workflow.model.WorkflowType;
import com.courtai.ai.workflow.pipeline.PipelineBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PipelineBuilderImpl implements PipelineBuilder {

    @Override
    public List<PipelineStage> buildPipeline(WorkflowType workflowType) {
        if (workflowType == null) {
            throw new WorkflowValidationException("WorkflowType cannot be null");
        }
        
        return switch (workflowType) {
            case OCR_WORKFLOW -> Arrays.asList(PipelineStage.OCR, PipelineStage.VALIDATION, PipelineStage.COMPLETED);
            case NLP_WORKFLOW -> Arrays.asList(PipelineStage.OCR, PipelineStage.NLP, PipelineStage.FEATURE_EXTRACTION, PipelineStage.COMPLETED);
            case PRIORITY_WORKFLOW -> Arrays.asList(PipelineStage.FEATURE_EXTRACTION, PipelineStage.PRIORITY, PipelineStage.VALIDATION, PipelineStage.COMPLETED);
            case TRUST_WORKFLOW -> Arrays.asList(PipelineStage.TRUST, PipelineStage.VALIDATION, PipelineStage.COMPLETED);
            case DUPLICATE_WORKFLOW -> Arrays.asList(PipelineStage.FEATURE_EXTRACTION, PipelineStage.DUPLICATE, PipelineStage.COMPLETED);
            case RECOMMENDATION_WORKFLOW -> Arrays.asList(PipelineStage.FEATURE_EXTRACTION, PipelineStage.RECOMMENDATION, PipelineStage.EXPLAINABILITY, PipelineStage.VALIDATION, PipelineStage.COMPLETED);
            case EXPLAINABILITY_WORKFLOW -> Arrays.asList(PipelineStage.EXPLAINABILITY, PipelineStage.VALIDATION, PipelineStage.COMPLETED);
            case COMPOSITE_WORKFLOW -> Arrays.asList(
                    PipelineStage.OCR, 
                    PipelineStage.NLP, 
                    PipelineStage.FEATURE_EXTRACTION, 
                    PipelineStage.DUPLICATE, 
                    PipelineStage.PRIORITY, 
                    PipelineStage.TRUST, 
                    PipelineStage.RECOMMENDATION, 
                    PipelineStage.EXPLAINABILITY, 
                    PipelineStage.VALIDATION, 
                    PipelineStage.AGGREGATION, 
                    PipelineStage.COMPLETED
            );
        };
    }
}
