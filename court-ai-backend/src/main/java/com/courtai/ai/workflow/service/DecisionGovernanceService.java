package com.courtai.ai.workflow.service;

import com.courtai.ai.workflow.model.Workflow;
import java.util.Map;

/**
 * Governance layer ensuring all AI outputs meet compliance, confidence, and trust standards
 * before being accepted into the workflow context.
 */
public interface DecisionGovernanceService {
    
    /**
     * Inspects intermediate outputs of a stage to ensure they are safe and meet thresholds.
     */
    boolean validateStageOutput(Workflow workflow, String stageName, Map<String, Object> output);
    
    /**
     * Inspects the final aggregated result to determine if human review is required
     * or if the AI recommendation can be safely propagated.
     */
    boolean requiresHumanReview(Workflow workflow, Map<String, Object> finalResult);
}
