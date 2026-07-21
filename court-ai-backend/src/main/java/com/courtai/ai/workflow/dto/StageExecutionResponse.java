package com.courtai.ai.workflow.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class StageExecutionResponse {
    private String stageName;
    private String status;
    private Map<String, Object> output;
}
