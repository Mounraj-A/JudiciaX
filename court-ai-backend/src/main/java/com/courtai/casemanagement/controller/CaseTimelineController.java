package com.courtai.casemanagement.controller;

import com.courtai.casemanagement.dto.CaseTimelineResponse;
import com.courtai.casemanagement.service.CaseTimelineService;
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
 * Returns the ordered lifecycle timeline of a case.
 * <p>Base path: {@code /api/v1/cases/{uuid}/timeline}</p>
 */
@RestController
@RequestMapping("/cases/{uuid}/timeline")
@RequiredArgsConstructor
@Tag(name = "Case Management", description = "Ordered case lifecycle timeline")
@SecurityRequirement(name = "bearerAuth")
public class CaseTimelineController {

    private final CaseTimelineService timelineService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get the complete ordered timeline of a case",
               description = "Returns all lifecycle events from creation to current state, "
                           + "ordered oldest → newest.")
    public ResponseEntity<ApiResponse<List<CaseTimelineResponse>>> getTimeline(
            @PathVariable String uuid) {
        return ResponseEntity.ok(ApiResponse.success("Timeline retrieved",
                timelineService.getTimeline(uuid)));
    }
}
