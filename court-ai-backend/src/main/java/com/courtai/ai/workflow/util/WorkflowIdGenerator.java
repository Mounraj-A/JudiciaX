package com.courtai.ai.workflow.util;

import java.util.UUID;

public final class WorkflowIdGenerator {

    private WorkflowIdGenerator() {}

    public static String generateWorkflowUuid() {
        return "WF-" + UUID.randomUUID().toString().toUpperCase();
    }
}
