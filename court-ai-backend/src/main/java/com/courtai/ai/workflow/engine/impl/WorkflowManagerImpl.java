package com.courtai.ai.workflow.engine.impl;

import com.courtai.ai.workflow.dto.WorkflowRequest;
import com.courtai.ai.workflow.engine.WorkflowManager;
import com.courtai.ai.workflow.exception.WorkflowException;
import com.courtai.ai.workflow.executor.PipelineExecutor;
import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.state.StateMachineService;
import com.courtai.ai.workflow.state.WorkflowState;
import com.courtai.ai.workflow.util.WorkflowIdGenerator;
import com.courtai.ai.workflow.validator.WorkflowValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowManagerImpl implements WorkflowManager {

    private final WorkflowValidationService validationService;
    private final StateMachineService stateMachine;
    private final PipelineExecutor pipelineExecutor;
    
    // In-memory store for Phase 2
    private final Map<String, Workflow> workflowStore = new ConcurrentHashMap<>();

    @Override
    public Workflow startWorkflow(WorkflowRequest request, String createdBy) {
        validationService.validateRequest(request);
        
        Workflow workflow = Workflow.builder()
                .workflowUuid(WorkflowIdGenerator.generateWorkflowUuid())
                .caseUuid(request.getCaseUuid())
                .documentUuid(request.getDocumentUuid())
                .workflowType(request.getWorkflowType())
                .priority(request.getPriority())
                .createdBy(createdBy)
                .pipelineVersion("v1.0")
                .modelVersion("stable")
                .build();
        
        stateMachine.transitionTo(workflow, WorkflowState.CREATED);
        workflowStore.put(workflow.getWorkflowUuid(), workflow);
        
        stateMachine.transitionTo(workflow, WorkflowState.VALIDATED);
        stateMachine.transitionTo(workflow, WorkflowState.QUEUED);
        
        log.info("Starting execution for workflow: {}", workflow.getWorkflowUuid());
        pipelineExecutor.execute(workflow); // Async execution
        
        return workflow;
    }

    @Override
    public Workflow pauseWorkflow(String workflowUuid) {
        Workflow workflow = getWorkflow(workflowUuid);
        stateMachine.transitionTo(workflow, WorkflowState.WAITING);
        return workflow;
    }

    @Override
    public Workflow resumeWorkflow(String workflowUuid) {
        Workflow workflow = getWorkflow(workflowUuid);
        stateMachine.transitionTo(workflow, WorkflowState.RUNNING);
        pipelineExecutor.execute(workflow);
        return workflow;
    }

    @Override
    public Workflow cancelWorkflow(String workflowUuid) {
        Workflow workflow = getWorkflow(workflowUuid);
        stateMachine.transitionTo(workflow, WorkflowState.CANCELLED);
        return workflow;
    }

    @Override
    public Workflow restartWorkflow(String workflowUuid) {
        // Typically restarting implies archiving old and starting new, or clearing state.
        // For simplicity in Phase 2, we just clear history and retry.
        return retryWorkflow(workflowUuid);
    }

    @Override
    public Workflow retryWorkflow(String workflowUuid) {
        Workflow workflow = getWorkflow(workflowUuid);
        stateMachine.transitionTo(workflow, WorkflowState.RETRYING);
        workflow.setRetryCount(workflow.getRetryCount() + 1);
        pipelineExecutor.execute(workflow);
        return workflow;
    }

    @Override
    public Workflow archiveWorkflow(String workflowUuid) {
        Workflow workflow = getWorkflow(workflowUuid);
        stateMachine.transitionTo(workflow, WorkflowState.ARCHIVED);
        return workflow;
    }

    @Override
    public Workflow getWorkflow(String workflowUuid) {
        Workflow workflow = workflowStore.get(workflowUuid);
        if (workflow == null) {
            throw new WorkflowException("Workflow not found: " + workflowUuid);
        }
        return workflow;
    }
}
