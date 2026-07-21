package com.courtai.ai.workflow.model;

import com.courtai.ai.workflow.state.WorkflowState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Memory-only Workflow Entity tracking the lifecycle of an AI workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {
    private String workflowUuid;
    private String caseUuid;
    private String documentUuid; // Optional
    private WorkflowType workflowType;
    private WorkflowState workflowState;
    private PipelineStage currentStage;
    
    private LocalDateTime startedTime;
    private LocalDateTime completedTime;
    private String createdBy;
    
    @Builder.Default
    private int retryCount = 0;
    
    private int priority;
    private String correlationId;
    private String pipelineVersion;
    private String modelVersion;
    
    // Tracks the execution details over time (Memory only list)
    @Builder.Default
    private List<String> executionHistory = new ArrayList<>();

    // Internal execution context to hold intermediate results between pipeline stages
    @Builder.Default
    private Map<String, Object> executionContext = new ConcurrentHashMap<>();
    
    public void addHistory(String event) {
        this.executionHistory.add(LocalDateTime.now() + " - " + event);
    }
}
