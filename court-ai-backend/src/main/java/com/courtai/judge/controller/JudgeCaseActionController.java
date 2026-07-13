package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.DisposeRequest;
import com.courtai.judge.dto.JudgeCaseResponse;
import com.courtai.judge.dto.ReserveJudgmentRequest;
import com.courtai.judge.service.JudgeCaseActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Judicial decision controller for the judge portal.
 * Handles the terminal actions: reserve judgment, dispose case, reopen case.
 */
@RestController
@RequestMapping("/judge/cases/{caseUuid}")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Judicial Decisions")
public class JudgeCaseActionController {

    private final JudgeCaseActionService actionService;

    @PutMapping("/reserve")
    @Operation(summary = "Reserve judgment",
               description = "Reserves judgment on a case. "
                           + "Valid from: JUDGE_ASSIGNED, HEARING_SCHEDULED, IN_PROGRESS, ADJOURNED. "
                           + "Sets status to JUDGEMENT_RESERVED. Notifies advocates and clerk.")
    public ResponseEntity<ApiResponse<JudgeCaseResponse>> reserveJudgment(
            @PathVariable String caseUuid,
            @Valid @RequestBody ReserveJudgmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Judgment reserved",
                actionService.reserveJudgment(caseUuid, request)));
    }

    @PutMapping("/dispose")
    @Operation(summary = "Dispose case",
               description = "Disposes (closes) a case after judgment. "
                           + "Valid from: JUDGEMENT_RESERVED. "
                           + "Sets status to DISPOSED. Notifies all parties.")
    public ResponseEntity<ApiResponse<JudgeCaseResponse>> disposeCase(
            @PathVariable String caseUuid,
            @Valid @RequestBody DisposeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Case disposed",
                actionService.disposeCase(caseUuid, request)));
    }

    @PutMapping("/reopen")
    @Operation(summary = "Reopen a disposed case",
               description = "Reopens a disposed case — only allowed under specific business conditions "
                           + "(e.g., higher-court remand). Sets status back to IN_PROGRESS.")
    public ResponseEntity<ApiResponse<JudgeCaseResponse>> reopenCase(
            @PathVariable String caseUuid,
            @Parameter(description = "Reason for reopening the case")
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.success("Case reopened",
                actionService.reopenCase(caseUuid, reason)));
    }
}
