package com.courtai.ai.workflow.model;

public enum PipelineStage {
    OCR,
    NLP,
    FEATURE_EXTRACTION,
    PRIORITY,
    TRUST,
    DUPLICATE,
    RECOMMENDATION,
    EXPLAINABILITY,
    VALIDATION,
    AGGREGATION,
    COMPLETED
}
