package com.courtai.casemanagement.controller;

import com.courtai.casemanagement.dto.CaseAssignmentRequest;
import com.courtai.casemanagement.dto.CaseTransferRequest;
import com.courtai.casemanagement.service.CaseAssignmentService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Manages all case assignments and transfers.
 * <p>Base path: {@code /api/v1/cases/{uuid}/assignment}</p>
 */
@RestController
@RequestMapping("/cases/{uuid}/assignment")
@RequiredArgsConstructor
@Tag(name = "Case Management", description = "Judge, Clerk, Court, and Bench assignment and transfer")
@SecurityRequirement(name = "bearerAuth")
public class CaseAssignmentController {

    private final CaseAssignmentService assignmentService;

    @PostMapping("/judge")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Assign a judge to the case")
    public ResponseEntity<ApiResponse<Void>> assignJudge(
            @PathVariable String uuid,
            @Valid @RequestBody CaseAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        assignmentService.assignJudge(uuid, request, actor.getUserUuid(), actor.getRoleName());
        return ResponseEntity.ok(ApiResponse.success("Judge assigned"));
    }

    @PutMapping("/judge/transfer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Transfer a case to a different judge")
    public ResponseEntity<ApiResponse<Void>> transferJudge(
            @PathVariable String uuid,
            @Valid @RequestBody CaseTransferRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        assignmentService.transferJudge(uuid, request, actor.getUserUuid(), actor.getRoleName());
        return ResponseEntity.ok(ApiResponse.success("Judge transferred"));
    }

    @PostMapping("/clerk")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Assign a clerk for case scrutiny")
    public ResponseEntity<ApiResponse<Void>> assignClerk(
            @PathVariable String uuid,
            @Valid @RequestBody CaseAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        assignmentService.assignClerk(uuid, request, actor.getUserUuid(), actor.getRoleName());
        return ResponseEntity.ok(ApiResponse.success("Clerk assigned"));
    }

    @PutMapping("/clerk/transfer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Transfer a case to a different clerk")
    public ResponseEntity<ApiResponse<Void>> transferClerk(
            @PathVariable String uuid,
            @Valid @RequestBody CaseTransferRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        assignmentService.transferClerk(uuid, request, actor.getUserUuid(), actor.getRoleName());
        return ResponseEntity.ok(ApiResponse.success("Clerk transferred"));
    }

    @PutMapping("/court/transfer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Transfer a case to a different court")
    public ResponseEntity<ApiResponse<Void>> transferCourt(
            @PathVariable String uuid,
            @Valid @RequestBody CaseTransferRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        assignmentService.transferCourt(uuid, request, actor.getUserUuid(), actor.getRoleName());
        return ResponseEntity.ok(ApiResponse.success("Case transferred to new court"));
    }

    @PostMapping("/bench")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Assign the case to a specific bench")
    public ResponseEntity<ApiResponse<Void>> assignBench(
            @PathVariable String uuid,
            @Valid @RequestBody CaseAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        assignmentService.assignBench(uuid, request, actor.getUserUuid(), actor.getRoleName());
        return ResponseEntity.ok(ApiResponse.success("Bench assigned"));
    }
}
