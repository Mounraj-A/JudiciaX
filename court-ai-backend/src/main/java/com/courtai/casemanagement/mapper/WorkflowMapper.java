package com.courtai.casemanagement.mapper;

import com.courtai.casefile.entity.CaseFile;
import com.courtai.casemanagement.dto.CaseWorkflowResponse;
import com.courtai.casemanagement.service.CaseWorkflowService;
import com.courtai.common.enums.CaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Maps {@link CaseFile} to {@link CaseWorkflowResponse} using the workflow service. */
@Component
@RequiredArgsConstructor
public class WorkflowMapper {

    private static final Set<CaseStatus> TERMINAL = Set.of(
            CaseStatus.ARCHIVED, CaseStatus.CANCELLED, CaseStatus.REJECTED, CaseStatus.DISPOSED
    );

    private static final Set<CaseStatus> READ_ONLY = Set.of(
            CaseStatus.ARCHIVED, CaseStatus.CANCELLED
    );

    private final CaseWorkflowService workflowService;

    public CaseWorkflowResponse toResponse(CaseFile c) {
        List<CaseStatus> allowed = workflowService.getAllowedTransitions(c.getStatus());
        boolean terminal = TERMINAL.contains(c.getStatus());
        boolean readOnly  = READ_ONLY.contains(c.getStatus());

        return CaseWorkflowResponse.builder()
                .caseUuid(c.getUuid())
                .caseNumber(c.getCaseNumber())
                .currentStatus(c.getStatus())
                .currentStatusLabel(c.getStatus().name())
                .allowedTransitions(allowed)
                .allowedTransitionLabels(allowed.stream()
                        .map(CaseStatus::name).collect(Collectors.toList()))
                .isTerminal(terminal)
                .isReadOnly(readOnly)
                .workflowMessage(buildMessage(c.getStatus(), terminal, readOnly))
                .build();
    }

    private String buildMessage(CaseStatus status, boolean terminal, boolean readOnly) {
        if (readOnly)  return "This case is archived and no further actions are permitted.";
        if (terminal)  return "This case has reached a terminal state: " + status.name();
        return "Case is active. " + workflowService.getAllowedTransitions(status).size()
                + " workflow action(s) available.";
    }
}
