package com.courtai.advocate.controller;

import com.courtai.advocate.dto.CaseResponse;
import com.courtai.advocate.dto.CaseSummaryResponse;
import com.courtai.advocate.dto.CreateCaseRequest;
import com.courtai.advocate.dto.UpdateCaseRequest;
import com.courtai.advocate.service.AdvocateCaseService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for advocate case management.
 *
 * <p>Advocates can only access their own cases — ownership enforced at the service layer.</p>
 */
@RestController
@RequestMapping("/advocate/cases")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADVOCATE')")
@Tag(name = "Advocate Module")
public class AdvocateCaseController {

    private final AdvocateCaseService caseService;

    @PostMapping
    @Operation(summary = "File a new case")
    public ResponseEntity<ApiResponse<CaseResponse>> createCase(
            @Valid @RequestBody CreateCaseRequest request) {
        CaseResponse response = caseService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Case filed successfully", response));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get case statistics for dashboard")
    public ResponseEntity<ApiResponse<com.courtai.advocate.dto.AdvocateCaseStatisticsResponse>> getCaseStatistics() {
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved", caseService.getCaseStatistics()));
    }

    @GetMapping
    @Operation(summary = "List my cases")
    public ResponseEntity<ApiResponse<Page<CaseSummaryResponse>>> getMyCases(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortBy));
        return ResponseEntity.ok(ApiResponse.success("Cases retrieved", caseService.getMyCases(pageable)));
    }

    @GetMapping("/{caseUuid}")
    @Operation(summary = "Get case details")
    public ResponseEntity<ApiResponse<CaseResponse>> getCaseByUuid(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Case retrieved", caseService.getCaseByUuid(caseUuid)));
    }

    @PutMapping("/{caseUuid}")
    @Operation(summary = "Update a case")
    public ResponseEntity<ApiResponse<CaseResponse>> updateCase(
            @PathVariable String caseUuid,
            @Valid @RequestBody UpdateCaseRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Case updated", caseService.updateCase(caseUuid, request)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filter cases by status")
    public ResponseEntity<ApiResponse<Page<CaseSummaryResponse>>> getCasesByStatus(
            @PathVariable CaseStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success(
                "Cases filtered by status: " + status,
                caseService.getCasesByStatus(status, pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search my cases by keyword")
    public ResponseEntity<ApiResponse<Page<CaseSummaryResponse>>> searchCases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success(
                "Search results for: " + keyword,
                caseService.searchCases(keyword, pageable)));
    }

    @PostMapping("/{caseUuid}/parties")
    @Operation(summary = "Add a party (petitioner or respondent) to a case")
    public ResponseEntity<ApiResponse<Void>> addCaseParty(
            @PathVariable String caseUuid,
            @Valid @RequestBody com.courtai.advocate.dto.PartyRequest request) {
        caseService.addCaseParty(caseUuid, request);
        return ResponseEntity.ok(ApiResponse.success("Party added to case successfully", null));
    }

    @PostMapping("/{caseUuid}/legal-info")
    @Operation(summary = "Save legal information for a case")
    public ResponseEntity<ApiResponse<Void>> saveLegalInfo(
            @PathVariable String caseUuid,
            @Valid @RequestBody com.courtai.advocate.dto.LegalInfoRequest request) {
        caseService.saveLegalInfo(caseUuid, request);
        return ResponseEntity.ok(ApiResponse.success("Legal info saved successfully", null));
    }

    @PutMapping("/{caseUuid}/submit")
    @Operation(summary = "Submit a draft case")
    public ResponseEntity<ApiResponse<CaseResponse>> submitCase(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Case submitted", caseService.submitCase(caseUuid)));
    }

    @GetMapping("/{caseUuid}/timeline")
    @Operation(summary = "Get the activity timeline for a case")
    public ResponseEntity<ApiResponse<java.util.List<com.courtai.advocate.dto.CaseTimelineResponse>>> getTimeline(
            @PathVariable String caseUuid) {
        return ResponseEntity.ok(ApiResponse.success("Case timeline retrieved", caseService.getTimeline(caseUuid)));
    }
}
