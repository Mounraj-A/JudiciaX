package com.courtai.ai.workflow.service.impl;

import com.courtai.ai.workflow.model.Workflow;
import com.courtai.ai.workflow.service.ResearchTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ResearchTrackingServiceImpl implements ResearchTrackingService {

    @Override
    public void trackWorkflowExecution(Workflow workflow) {
        if (workflow.getStartedTime() != null && workflow.getCompletedTime() != null) {
            long latency = Duration.between(workflow.getStartedTime(), workflow.getCompletedTime()).toMillis();
            
            log.info("Research Metrics [Workflow: {}] -> Latency: {} ms, Retries: {}, Final State: {}", 
                    workflow.getWorkflowUuid(), latency, workflow.getRetryCount(), workflow.getWorkflowState());
            
            // Note: In Phase 3 or later, this would persist to the dedicated Research Analytics tables.
        }
    }

    @Override
    public void trackJudicialFeedback(String workflowUuid, boolean accepted, boolean modified, String feedback) {
        log.info("Research Feedback Hook [Workflow: {}] -> Accepted: {}, Modified: {}, Feedback: {}", 
                workflowUuid, accepted, modified, feedback);
    }
}
