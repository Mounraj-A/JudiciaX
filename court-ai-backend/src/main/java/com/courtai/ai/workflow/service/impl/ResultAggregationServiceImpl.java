package com.courtai.ai.workflow.service.impl;

import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.service.ResultAggregationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResultAggregationServiceImpl implements ResultAggregationService {

    @Override
    public Map<String, Object> aggregateResults(Workflow workflow) {
        Map<String, Object> aggregated = new HashMap<>();
        
        // Simulating the extraction of various AI outputs stored in the workflow execution context
        Map<String, Object> context = workflow.getExecutionContext();
        
        aggregated.put("workflowUuid", workflow.getWorkflowUuid());
        aggregated.put("caseUuid", workflow.getCaseUuid());
        aggregated.put("finalStatus", workflow.getWorkflowState().name());
        
        if (context.containsKey("ocrOutput")) {
            aggregated.put("extractedText", context.get("ocrOutput"));
        }
        if (context.containsKey("nlpOutput")) {
            aggregated.put("semanticEntities", context.get("nlpOutput"));
        }
        if (context.containsKey("priorityScore")) {
            aggregated.put("priorityScore", context.get("priorityScore"));
        }
        if (context.containsKey("trustScore")) {
            aggregated.put("trustScore", context.get("trustScore"));
        }
        
        return aggregated;
    }
}
