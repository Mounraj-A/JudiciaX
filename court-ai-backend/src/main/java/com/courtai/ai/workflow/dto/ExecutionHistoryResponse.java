package com.courtai.ai.workflow.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ExecutionHistoryResponse {
    private String workflowUuid;
    private List<String> history;
}
