package com.courtai.ai.workflow.executor;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Task Scheduler for future asynchronous and parallel execution.
 */
@Component("workflowTaskScheduler")
public class TaskScheduler {
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    /**
     * Schedules a task to run asynchronously.
     */
    public <T> CompletableFuture<T> scheduleTask(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executorService);
    }
}
