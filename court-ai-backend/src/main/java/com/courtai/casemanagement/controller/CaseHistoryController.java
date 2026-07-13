package com.courtai.casemanagement.controller;

import com.courtai.casemanagement.dto.CaseHistoryResponse;
import com.courtai.casemanagement.service.CaseHistoryService;
import com.courtai.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Returns the immutable status history of a case.
 * <p>Base path: {@code /api/v1/cases/{uuid}/history}</p>
 */
@RestController
@RequestMapping("/cases/{uuid}/history")
@RequiredArgsConstructor
@Tag(name = "Case Management", description = "Immutable status and assignment change history")
@SecurityRequirement(name = "bearerAuth")
public class CaseHistoryController {

    private final CaseHistoryService historyService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get full status history of a case",
               description = "Returns every status transition from creation to present, newest first.")
    public ResponseEntity<ApiResponse<List<CaseHistoryResponse>>> getStatusHistory(
            @PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Status history retrieved",
                historyService.getStatusHistory(uuid)));
    }

    @GetMapping("/assignments")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_JUDGE','ROLE_CLERK')")
    @Operation(summary = "Get judge assignment history of a case")
    public ResponseEntity<ApiResponse<List<CaseHistoryResponse>>> getAssignmentHistory(
            @PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Assignment history retrieved",
                historyService.getAssignmentHistory(uuid)));
    }
}
