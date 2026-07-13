package com.courtai.admin.controller;

import com.courtai.admin.dto.AssignJudgeRequest;
import com.courtai.admin.dto.JudgeWorkloadResponse;
import com.courtai.admin.service.JudgeAdministrationService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Judge administration controller.
 * <p>Base path: {@code /api/v1/admin/judges}</p>
 */
@RestController
@RequestMapping("/admin/judges")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Module — Judge Administration", description = "Judge assignment, transfer, and workload")
@SecurityRequirement(name = "bearerAuth")
public class JudgeAdministrationController {

    private final JudgeAdministrationService judgeService;

    @GetMapping("/workloads")
    @Operation(summary = "List all judge workloads",
               description = "Returns assigned case counts, active cases, disposed cases, and reserved judgments for every judge.")
    public ResponseEntity<ApiResponse<Page<JudgeWorkloadResponse>>> getWorkloads(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Judge workloads retrieved",
                judgeService.getJudgeWorkloads(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{judgeUserUuid}/workload")
    @Operation(summary = "Get workload for a specific judge")
    public ResponseEntity<ApiResponse<JudgeWorkloadResponse>> getWorkload(
            @PathVariable String judgeUserUuid) {
        return ResponseEntity.ok(ApiResponse.success("Judge workload retrieved",
                judgeService.getJudgeWorkload(judgeUserUuid)));
    }

    @PostMapping("/assign")
    @Operation(summary = "Assign a judge to a case",
               description = "Admin assigns or re-assigns a judge to an open case. "
                           + "Validates case is not disposed or closed.")
    public ResponseEntity<ApiResponse<Void>> assignJudge(
            @Valid @RequestBody AssignJudgeRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        judgeService.assignJudgeToCase(request, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Judge assigned to case"));
    }

    @PutMapping("/{judgeUserUuid}/transfer")
    @Operation(summary = "Transfer a judge from one case to another",
               description = "Replaces the current judge assignment on a case. Validates open status.")
    public ResponseEntity<ApiResponse<Void>> transferJudge(
            @PathVariable String judgeUserUuid,
            @RequestParam String caseUuid,
            @RequestParam(required = false, defaultValue = "Administrative reassignment") String reason,
            @AuthenticationPrincipal UserPrincipal admin) {
        judgeService.transferJudge(caseUuid, judgeUserUuid, reason, admin.getUserUuid());
        return ResponseEntity.ok(ApiResponse.success("Judge transferred"));
    }
}
