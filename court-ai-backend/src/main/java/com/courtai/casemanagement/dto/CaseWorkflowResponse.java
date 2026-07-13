package com.courtai.casemanagement.dto;

import com.courtai.common.enums.CaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** Current workflow state of a case: status + valid next transitions. */
@Getter
@Builder
public class CaseWorkflowResponse {
    private String caseUuid;
    private String caseNumber;
    private CaseStatus currentStatus;
    private String currentStatusLabel;
    private List<CaseStatus> allowedTransitions;
    private List<String> allowedTransitionLabels;
    private boolean isTerminal;
    private boolean isReadOnly;
    private String workflowMessage;
}
