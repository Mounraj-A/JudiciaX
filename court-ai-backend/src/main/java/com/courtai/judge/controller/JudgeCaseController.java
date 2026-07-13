package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.common.enums.CaseStatus;
import com.courtai.judge.dto.JudgeCaseResponse;
import com.courtai.judge.dto.JudgeCaseSummaryResponse;
import com.courtai.judge.service.JudgeCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Case controller for the judge portal.
 * Read-only — judges cannot create or modify cases.
 */
@RestController
@RequestMapping("/judge/cases")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Case Review")
public class JudgeCaseController {

    private final JudgeCaseService caseService;

    @GetMapping
    @Operation(summary = "List assigned cases",
               description = "Returns all cases assigned to the current judge, paginated.")
    public ResponseEntity<ApiResponse<Page<JudgeCaseSummaryResponse>>> getAssignedCases(
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(defaultValue = "createdAt")  String sortBy,
            @RequestParam(defaultValue = "desc")       String direction) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortBy));
        return ResponseEntity.ok(ApiResponse.success("Assigned cases retrieved",
                caseService.getAssignedCases(pageable)));
    }

    @GetMapping("/{caseUuid}")
    @Operation(summary = "Get case detail",
               description = "Returns the full detail of a case assigned to the current judge.")
    public ResponseEntity<ApiResponse<JudgeCaseResponse>> getCaseDetail(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Case retrieved",
                caseService.getCaseByUuid(caseUuid)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search assigned cases by keyword")
    public ResponseEntity<ApiResponse<Page<JudgeCaseSummaryResponse>>> searchCases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success("Search results for: " + keyword,
                caseService.searchCases(keyword, pageable)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filter assigned cases by status")
    public ResponseEntity<ApiResponse<Page<JudgeCaseSummaryResponse>>> getCasesByStatus(
            @PathVariable CaseStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success("Cases with status: " + status,
                caseService.getCasesByStatus(status, pageable)));
    }
}
