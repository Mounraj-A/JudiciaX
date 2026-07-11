package com.courtai.clerk.controller;

import com.courtai.clerk.dto.*;
import com.courtai.clerk.service.CaseRegistrationService;
import com.courtai.clerk.service.ClerkCaseService;
import com.courtai.clerk.service.DuplicateDetectionService;
import com.courtai.common.dto.ApiResponse;
import com.courtai.common.enums.CaseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for clerk case scrutiny, registration, and duplicate detection.
 */
@RestController
@RequestMapping("/clerk/cases")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CLERK')")
@Tag(name = "Clerk Module")
public class ClerkCaseController {

    private final ClerkCaseService          caseService;
    private final CaseRegistrationService   registrationService;
    private final DuplicateDetectionService duplicateService;

    @GetMapping("/pending")
    @Operation(summary = "List pending scrutiny cases (SUBMITTED status)")
    public ResponseEntity<ApiResponse<Page<ClerkCaseSummaryResponse>>> getPendingCases(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success("Pending cases retrieved",
                caseService.getCases(List.of(CaseStatus.SUBMITTED), pageable)));
    }

    @GetMapping
    @Operation(summary = "List cases with optional status filter")
    public ResponseEntity<ApiResponse<Page<ClerkCaseSummaryResponse>>> getCases(
            @RequestParam(required = false) List<CaseStatus> status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success("Cases retrieved",
                caseService.getCases(status, pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Keyword search within clerk's court")
    public ResponseEntity<ApiResponse<Page<ClerkCaseSummaryResponse>>> searchCases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success("Search results",
                caseService.searchCases(keyword, pageable)));
    }

    @GetMapping("/{caseUuid}")
    @Operation(summary = "Get full case scrutiny detail")
    public ResponseEntity<ApiResponse<CaseScrutinyResponse>> getCaseDetail(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Case detail retrieved",
                caseService.getCaseDetail(caseUuid)));
    }

    @PutMapping("/{caseUuid}/open-scrutiny")
    @Operation(summary = "Open a SUBMITTED case for scrutiny → UNDER_SCRUTINY")
    public ResponseEntity<ApiResponse<CaseScrutinyResponse>> openScrutiny(
            @PathVariable String caseUuid,
            @Valid @RequestBody ScrutinyActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Case opened for scrutiny",
                caseService.openScrutiny(caseUuid, request.getRemarks())));
    }

    @PutMapping("/{caseUuid}/verify")
    @Operation(summary = "Mark case jurisdiction verified (pre-registration step)")
    public ResponseEntity<ApiResponse<CaseScrutinyResponse>> verifyCase(
            @PathVariable String caseUuid,
            @Valid @RequestBody ScrutinyActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Case verified",
                caseService.verifyCase(caseUuid, request)));
    }

    @PutMapping("/{caseUuid}/return")
    @Operation(summary = "Return case to advocate with remarks → RETURNED")
    public ResponseEntity<ApiResponse<CaseScrutinyResponse>> returnCase(
            @PathVariable String caseUuid,
            @Valid @RequestBody ScrutinyActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Case returned to advocate",
                caseService.returnCase(caseUuid, request)));
    }

    @PutMapping("/{caseUuid}/register")
    @Operation(summary = "Officially register a case → REGISTERED + generates official case number")
    public ResponseEntity<ApiResponse<CaseRegistrationResponse>> registerCase(
            @PathVariable String caseUuid,
            @Valid @RequestBody ScrutinyActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Case registered successfully",
                registrationService.registerCase(caseUuid, request.getRemarks())));
    }

    @PostMapping("/{caseUuid}/objections")
    @Operation(summary = "Raise an objection on a case")
    public ResponseEntity<ApiResponse<ObjectionResponse>> raiseObjection(
            @PathVariable String caseUuid,
            @Valid @RequestBody RaiseObjectionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Objection raised",
                caseService.raiseObjection(caseUuid, request)));
    }

    @GetMapping("/{caseUuid}/objections")
    @Operation(summary = "List all objections for a case")
    public ResponseEntity<ApiResponse<List<ObjectionResponse>>> getObjections(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Objections retrieved",
                caseService.getObjections(caseUuid)));
    }

    @GetMapping("/{caseUuid}/timeline")
    @Operation(summary = "Get status transition timeline for a case")
    public ResponseEntity<ApiResponse<List<CaseTimelineResponse>>> getCaseTimeline(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Timeline retrieved",
                caseService.getCaseTimeline(caseUuid)));
    }

    @PostMapping("/{caseUuid}/duplicate-check")
    @Operation(summary = "Run duplicate detection for a case")
    public ResponseEntity<ApiResponse<DuplicateCheckResponse>> checkDuplicates(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Duplicate check completed",
                duplicateService.checkForDuplicates(caseUuid)));
    }
}
