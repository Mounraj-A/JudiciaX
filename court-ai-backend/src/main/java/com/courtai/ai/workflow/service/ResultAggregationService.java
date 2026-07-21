package com.courtai.ai.workflow.service;

import com.courtai.ai.workflow.model.Workflow;

import java.util.Map;

public interface ResultAggregationService {
    
    /**
     * Aggregates intermediate stage outputs into a single cohesive final response map.
     */
    Map<String, Object> aggregateResults(Workflow workflow);
}
