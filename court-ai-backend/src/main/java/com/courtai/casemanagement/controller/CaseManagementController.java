package com.courtai.casemanagement.controller;

import com.courtai.casemanagement.dto.*;
import com.courtai.casemanagement.service.CaseManagementService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Central case lifecycle controller.
 * <p>Base path: {@code /api/v1/cases}</p>
 */
@RestController
@RequestMapping("/cases")
@RequiredArgsConstructor
@Tag(name = "Case Management", description = "Core case lifecycle: create, read, update, submit, archive, close, reopen, cancel, clone")
@SecurityRequirement(name = "bearerAuth")
public class CaseManagementController {

    private final CaseManagementService caseService;

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADVOCATE','ROLE_ADMIN')")
    @Operation(summary = "Create a new case in DRAFT status")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> createCase(
            @Valid @RequestBody CaseCreateRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Case created",
                        caseService.createCase(request, actor.getUserUuid(), actor.getRoleName())));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all cases (paginated)")
    public ResponseEntity<ApiResponse<Page<CaseSummaryResponse>>> listCases(
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "20")         int size,
            @RequestParam(defaultValue = "createdAt")  String sortBy,
            @RequestParam(defaultValue = "desc")       String direction) {
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(ApiResponse.success("Cases retrieved",
                caseService.listCases(PageRequest.of(page, size, sort))));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get full case details by UUID")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> getCase(@PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Case retrieved",
                caseService.getCase(uuid)));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADVOCATE','ROLE_ADMIN')")
    @Operation(summary = "Update a case (advocates: DRAFT only; admins: any editable status)")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> updateCase(
            @PathVariable String uuid,
            @Valid @RequestBody CaseUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Case updated",
                caseService.updateCase(uuid, request, actor.getUserUuid(), actor.getRoleName())));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADVOCATE','ROLE_ADMIN')")
    @Operation(summary = "Delete a DRAFT case (soft-delete)")
    public ResponseEntity<ApiResponse<Void>> deleteCase(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal actor) {
        caseService.deleteCase(uuid, actor.getUserUuid(), actor.getRoleName());
        return ResponseEntity.ok(ApiResponse.success("Case deleted"));
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    @PutMapping("/{uuid}/submit")
    @PreAuthorize("hasAnyAuthority('ROLE_ADVOCATE','ROLE_ADMIN')")
    @Operation(summary = "Submit a DRAFT case for court scrutiny")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> submitCase(
            @PathVariable String uuid,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Case submitted",
                caseService.submitCase(uuid, actor.getUserUuid(), actor.getRoleName())));
    }

    // ── Archive ───────────────────────────────────────────────────────────────

    @PutMapping("/{uuid}/archive")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Archive a DISPOSED or CLOSED case (read-only after archival)")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> archiveCase(
            @PathVariable String uuid,
            @Valid @RequestBody CaseArchiveRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Case archived",
                caseService.archiveCase(uuid, request, actor.getUserUuid(), actor.getRoleName())));
    }

    // ── Close ─────────────────────────────────────────────────────────────────

    @PutMapping("/{uuid}/close")
    @PreAuthorize("hasAnyAuthority('ROLE_JUDGE','ROLE_ADMIN')")
    @Operation(summary = "Close a DISPOSED case")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> closeCase(
            @PathVariable String uuid,
            @RequestParam(required = false, defaultValue = "Case formally closed") String reason,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Case closed",
                caseService.closeCase(uuid, actor.getUserUuid(), actor.getRoleName(), reason)));
    }

    // ── Reopen ────────────────────────────────────────────────────────────────

    @PutMapping("/{uuid}/reopen")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Reopen a CLOSED case (admin only)")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> reopenCase(
            @PathVariable String uuid,
            @RequestParam(required = false, defaultValue = "Administrative reopening") String reason,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Case reopened",
                caseService.reopenCase(uuid, actor.getUserUuid(), actor.getRoleName(), reason)));
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    @PutMapping("/{uuid}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_ADVOCATE','ROLE_ADMIN')")
    @Operation(summary = "Cancel a case before registration")
    public ResponseEntity<ApiResponse<CaseDetailsResponse>> cancelCase(
            @PathVariable String uuid,
            @Valid @RequestBody CaseCancelRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success("Case cancelled",
                caseService.cancelCase(uuid, request, actor.getUserUuid(), actor.getRoleName())));
    }

    // ── Clone ─────────────────────────────────────────────────────────────────

    @PostMapping("/{uuid}/clone")
    @PreAuthorize("hasAnyAuthority('ROLE_ADVOCATE','ROLE_ADMIN')")
    @Operation(summary = "Clone a DRAFT/SUBMITTED case (copies basic details, NOT documents/evidence/hearings)")
    public ResponseEntity<ApiResponse<CaseCloneResponse>> cloneCase(
            @PathVariable String uuid,
            @Valid @RequestBody CaseCloneRequest request,
            @AuthenticationPrincipal UserPrincipal actor) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Case cloned",
                        caseService.cloneCase(uuid, request, actor.getUserUuid(), actor.getRoleName())));
    }
}
