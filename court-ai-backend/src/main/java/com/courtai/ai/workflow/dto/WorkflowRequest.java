package com.courtai.ai.workflow.dto;

import com.courtai.ai.workflow.model.WorkflowType;
import lombok.Data;

@Data
public class WorkflowRequest {
    private String caseUuid;
    private String documentUuid;
    private WorkflowType workflowType;
    private int priority;
}
