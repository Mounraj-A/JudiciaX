package com.courtai.casemanagement.controller;

import com.courtai.casemanagement.dto.CaseWorkflowResponse;
import com.courtai.casemanagement.mapper.WorkflowMapper;
import com.courtai.casemanagement.service.CaseWorkflowService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.dto.ApiResponse;
import com.courtai.common.enums.CaseStatus;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes workflow state and executes validated status transitions.
 * <p>Base path: {@code /api/v1/cases/{uuid}/workflow}</p>
 */
@RestController
@RequestMapping("/cases/{uuid}/workflow")
@RequiredArgsConstructor
@Tag(name = "Case Management", description = "Workflow state machine: query and execute transitions")
@SecurityRequirement(name = "bearerAuth")
public class CaseWorkflowController {

    private final CaseWorkflowService    workflowService;
    private final WorkflowMapper         workflowMapper;
    private final CaseFileRepository     caseFileRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get the current workflow state and allowed transitions")
    public ResponseEntity<ApiResponse<CaseWorkflowResponse>> getWorkflow(@PathVariable String uuid) {
        CaseFile caseFile = loadCase(uuid);
        return ResponseEntity.ok(ApiResponse.success("Workflow state retrieved",
                workflowMapper.toResponse(caseFile)));
    }

    @PutMapping("/transition")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Execute a workflow transition",
               description = "Validates the requested transition against the state machine, "
                           + "records history, and triggers timeline + audit events.")
    public ResponseEntity<ApiResponse<CaseWorkflowResponse>> transition(
            @PathVariable String uuid,
            @RequestParam CaseStatus toStatus,
            @RequestParam(required = false, defaultValue = "") String reason,
            @AuthenticationPrincipal UserPrincipal actor) {
        workflowService.executeTransition(uuid, toStatus,
                actor.getUserUuid(), actor.getRoleName(), reason);
        return ResponseEntity.ok(ApiResponse.success("Transition executed",
                workflowMapper.toResponse(loadCase(uuid))));
    }

    private CaseFile loadCase(String uuid) {
        return caseFileRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", uuid));
    }
}
