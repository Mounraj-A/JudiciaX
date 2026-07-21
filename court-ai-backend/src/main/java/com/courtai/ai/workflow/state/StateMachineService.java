package com.courtai.ai.workflow.state;

import com.courtai.ai.workflow.model.Workflow;

public interface StateMachineService {
    
    /**
     * Validates and transitions the workflow to the target state.
     * Throws StateTransitionException if the transition is invalid.
     */
    void transitionTo(Workflow workflow, WorkflowState targetState);
}
