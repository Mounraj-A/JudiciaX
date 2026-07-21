package com.courtai.ai.workflow.controller;

import com.courtai.ai.workflow.dto.ExecutionHistoryResponse;
import com.courtai.ai.workflow.dto.WorkflowRequest;
import com.courtai.ai.workflow.dto.WorkflowResponse;
import com.courtai.ai.workflow.dto.WorkflowStatusResponse;
import com.courtai.ai.workflow.dto.WorkflowSummaryResponse;
import com.courtai.ai.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai/workflows")
@RequiredArgsConstructor
@Tag(name = "AI Workflow Engine", description = "Orchestrates AI Pipelines for Document Processing")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/start")
    @Operation(summary = "Start a new AI Workflow")
    public ResponseEntity<WorkflowResponse> startWorkflow(@RequestBody WorkflowRequest request) {
        // In a real app, createdBy is fetched from SecurityContext
        return ResponseEntity.ok(workflowService.startWorkflow(request, "SYSTEM"));
    }

    @PostMapping("/retry")
    @Operation(summary = "Retry a failed AI Workflow")
    public ResponseEntity<WorkflowResponse> retryWorkflow(@RequestParam String uuid) {
        return ResponseEntity.ok(workflowService.retryWorkflow(uuid));
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel an running AI Workflow")
    public ResponseEntity<WorkflowResponse> cancelWorkflow(@RequestParam String uuid) {
        return ResponseEntity.ok(workflowService.cancelWorkflow(uuid));
    }

    @PostMapping("/restart")
    @Operation(summary = "Restart an AI Workflow from the beginning")
    public ResponseEntity<WorkflowResponse> restartWorkflow(@RequestParam String uuid) {
        return ResponseEntity.ok(workflowService.restartWorkflow(uuid));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get Workflow details by UUID")
    public ResponseEntity<WorkflowStatusResponse> getWorkflow(@PathVariable String uuid) {
        return ResponseEntity.ok(workflowService.getWorkflowStatus(uuid));
    }

    @GetMapping
    @Operation(summary = "List all active Workflows")
    public ResponseEntity<List<WorkflowSummaryResponse>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    @GetMapping("/status")
    @Operation(summary = "Get Workflow status")
    public ResponseEntity<WorkflowStatusResponse> getWorkflowStatus(@RequestParam String uuid) {
        return ResponseEntity.ok(workflowService.getWorkflowStatus(uuid));
    }

    @GetMapping("/history")
    @Operation(summary = "Get Workflow execution history")
    public ResponseEntity<ExecutionHistoryResponse> getWorkflowHistory(@RequestParam String uuid) {
        return ResponseEntity.ok(workflowService.getWorkflowHistory(uuid));
    }
}
