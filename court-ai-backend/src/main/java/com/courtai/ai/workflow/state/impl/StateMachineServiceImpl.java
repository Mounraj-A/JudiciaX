package com.courtai.ai.workflow.state.impl;

import com.courtai.ai.workflow.exception.StateTransitionException;
import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.state.StateMachineService;
import com.courtai.ai.workflow.state.WorkflowState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class StateMachineServiceImpl implements StateMachineService {

    @Override
    public void transitionTo(Workflow workflow, WorkflowState targetState) {
        WorkflowState currentState = workflow.getWorkflowState();
        
        if (currentState == targetState) {
            return; // Already in target state
        }
        
        if (!isValidTransition(currentState, targetState)) {
            throw new StateTransitionException(
                String.format("Invalid state transition from %s to %s for workflow %s", 
                    currentState, targetState, workflow.getWorkflowUuid())
            );
        }

        log.info("Transitioning workflow {} from {} to {}", workflow.getWorkflowUuid(), currentState, targetState);
        workflow.setWorkflowState(targetState);
        workflow.addHistory("State transitioned to " + targetState);
        
        if (targetState == WorkflowState.RUNNING && workflow.getStartedTime() == null) {
            workflow.setStartedTime(LocalDateTime.now());
        }
        
        if (isTerminalState(targetState)) {
            workflow.setCompletedTime(LocalDateTime.now());
        }
    }

    private boolean isValidTransition(WorkflowState current, WorkflowState target) {
        if (current == null) return target == WorkflowState.CREATED;
        
        return switch (current) {
            case CREATED -> target == WorkflowState.VALIDATED || target == WorkflowState.CANCELLED;
            case VALIDATED -> target == WorkflowState.QUEUED || target == WorkflowState.CANCELLED;
            case QUEUED -> target == WorkflowState.RUNNING || target == WorkflowState.CANCELLED;
            case RUNNING -> target == WorkflowState.WAITING || target == WorkflowState.COMPLETED || 
                            target == WorkflowState.PARTIALLY_COMPLETED || target == WorkflowState.FAILED || 
                            target == WorkflowState.CANCELLED;
            case WAITING -> target == WorkflowState.RUNNING || target == WorkflowState.CANCELLED;
            case FAILED -> target == WorkflowState.RETRYING || target == WorkflowState.ARCHIVED;
            case RETRYING -> target == WorkflowState.RUNNING || target == WorkflowState.FAILED || target == WorkflowState.CANCELLED;
            case COMPLETED, PARTIALLY_COMPLETED, CANCELLED -> target == WorkflowState.ARCHIVED;
            case ARCHIVED -> false; // Terminal state, no outbound transitions
        };
    }
    
    private boolean isTerminalState(WorkflowState state) {
        return state == WorkflowState.COMPLETED || 
               state == WorkflowState.PARTIALLY_COMPLETED || 
               state == WorkflowState.FAILED || 
               state == WorkflowState.CANCELLED;
    }
}
