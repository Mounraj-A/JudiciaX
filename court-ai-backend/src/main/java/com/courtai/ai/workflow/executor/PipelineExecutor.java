package com.courtai.ai.workflow.executor;

import com.courtai.ai.workflow.model.Workflow;

import java.util.concurrent.CompletableFuture;

public interface PipelineExecutor {
    
    /**
     * Executes the workflow pipeline asynchronously.
     * Delegates to the underlying Gateway/FastAPI models and iterates through PipelineBuilder stages.
     */
    CompletableFuture<Void> execute(Workflow workflow);
}
