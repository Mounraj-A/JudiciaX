package com.courtai.ai.workflow.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PipelineResponse {
    private String workflowUuid;
    private String pipelineVersion;
    private List<String> expectedStages;
}
