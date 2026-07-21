package com.courtai.ai.workflow.service.impl;

import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.service.DecisionGovernanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class DecisionGovernanceServiceImpl implements DecisionGovernanceService {

    @Override
    public boolean validateStageOutput(Workflow workflow, String stageName, Map<String, Object> output) {
        if (output == null || output.isEmpty()) {
            log.warn("Stage {} returned empty output for workflow {}", stageName, workflow.getWorkflowUuid());
            // Depending on strictness, we might return false here. Assuming false for empty output.
            return false;
        }
        
        // Placeholder for advanced governance rules.
        // E.g., checking confidence score > 0.85
        Object confidenceObj = output.get("confidence");
        if (confidenceObj instanceof Number) {
            double confidence = ((Number) confidenceObj).doubleValue();
            if (confidence < 0.60) {
                log.warn("Governance rejected output from stage {} due to low confidence: {}", stageName, confidence);
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean requiresHumanReview(Workflow workflow, Map<String, Object> finalResult) {
        // Governance logic to force human review for critical cases or low overall trust scores.
        // Defaulting to true for safety in judicial context.
        Object trustScore = finalResult.get("trustScore");
        if (trustScore instanceof Number) {
            return ((Number) trustScore).doubleValue() < 0.90;
        }
        return true; // Always require human review if trust score is unverified
    }
}
