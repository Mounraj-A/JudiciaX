package com.courtai.ai.workflow.executor.impl;

import com.courtai.ai.workflow.executor.PipelineExecutor;
import com.courtai.ai.workflow.model.PipelineStage;
import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.pipeline.PipelineBuilder;
import com.courtai.ai.workflow.state.StateMachineService;
import com.courtai.ai.workflow.state.WorkflowState;
import com.courtai.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineExecutorImpl implements PipelineExecutor {

    private final PipelineBuilder pipelineBuilder;
    private final StateMachineService stateMachine;
    private final AuditService auditService; // Uses existing audit service

    @Async
    @Override
    public CompletableFuture<Void> execute(Workflow workflow) {
        return CompletableFuture.runAsync(() -> {
            try {
                stateMachine.transitionTo(workflow, WorkflowState.RUNNING);
                List<PipelineStage> stages = pipelineBuilder.buildPipeline(workflow.getWorkflowType());
                
                for (PipelineStage stage : stages) {
                    executeStage(workflow, stage);
                }
                
                stateMachine.transitionTo(workflow, WorkflowState.COMPLETED);
                auditService.logSuccess(
                        "WORKFLOW_COMPLETED", 
                        "WORKFLOW", 
                        workflow.getWorkflowUuid(), 
                        "Workflow completed successfully"
                );
            } catch (Exception e) {
                log.error("Pipeline execution failed for workflow {}", workflow.getWorkflowUuid(), e);
                stateMachine.transitionTo(workflow, WorkflowState.FAILED);
                auditService.logFailure(
                        "WORKFLOW_FAILED", 
                        "WORKFLOW", 
                        workflow.getWorkflowUuid(), 
                        "Workflow failed: " + e.getMessage()
                );
            }
        });
    }
    
    private void executeStage(Workflow workflow, PipelineStage stage) {
        log.info("Executing stage {} for workflow {}", stage, workflow.getWorkflowUuid());
        workflow.setCurrentStage(stage);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Placeholder: Here it would interact with the AI Gateway service layer.
            // Since this phase explicitly forbids actual calls or OCR logic, we simulate orchestration execution.
            Thread.sleep(100); // Simulate processing latency
            
            long latency = System.currentTimeMillis() - startTime;
            workflow.addHistory(String.format("Stage %s completed in %d ms", stage.name(), latency));
            
            auditService.logSuccess(
                    "WORKFLOW_STAGE_COMPLETED", 
                    "WORKFLOW_STAGE", 
                    workflow.getWorkflowUuid(), 
                    "Stage " + stage.name() + " completed in " + latency + " ms"
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Pipeline stage execution interrupted", e);
        }
    }
}
