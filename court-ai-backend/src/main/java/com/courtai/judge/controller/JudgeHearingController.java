package com.courtai.judge.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.judge.dto.AdjournHearingRequest;
import com.courtai.judge.dto.JudgeHearingResponse;
import com.courtai.judge.dto.ScheduleHearingRequest;
import com.courtai.judge.dto.UpdateHearingRequest;
import com.courtai.judge.service.JudgeHearingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Hearing management controller for the judge portal.
 * The judge can schedule, update, adjourn, and complete hearings.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_JUDGE')")
@Tag(name = "Judge Module", description = "Judge Portal — Hearing Management")
public class JudgeHearingController {

    private final JudgeHearingService hearingService;

    // ── Flat hearing endpoints ────────────────────────────────────────────────

    @GetMapping("/judge/hearings")
    @Operation(summary = "List all my hearings",
               description = "Returns all hearings across all cases assigned to the current judge.")
    public ResponseEntity<ApiResponse<List<JudgeHearingResponse>>> getMyHearings() {
        return ResponseEntity.ok(ApiResponse.success("Hearings retrieved",
                hearingService.getMyHearings()));
    }

    @GetMapping("/judge/hearings/{hearingUuid}")
    @Operation(summary = "Get hearing detail")
    public ResponseEntity<ApiResponse<JudgeHearingResponse>> getHearing(
            @PathVariable String hearingUuid) {
        return ResponseEntity.ok(ApiResponse.success("Hearing retrieved",
                hearingService.getHearingByUuid(hearingUuid)));
    }

    @PostMapping("/judge/hearings")
    @Operation(summary = "Schedule a hearing",
               description = "Schedules a new hearing for a case assigned to the judge. "
                           + "Validates: case not disposed, no time-room conflicts.")
    public ResponseEntity<ApiResponse<JudgeHearingResponse>> scheduleHearing(
            @Valid @RequestBody ScheduleHearingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Hearing scheduled",
                        hearingService.scheduleHearing(request)));
    }

    @PutMapping("/judge/hearings/{hearingUuid}")
    @Operation(summary = "Update a hearing")
    public ResponseEntity<ApiResponse<JudgeHearingResponse>> updateHearing(
            @PathVariable String hearingUuid,
            @Valid @RequestBody UpdateHearingRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Hearing updated",
                hearingService.updateHearing(hearingUuid, request)));
    }

    @PutMapping("/judge/hearings/{hearingUuid}/adjourn")
    @Operation(summary = "Adjourn a hearing",
               description = "Sets hearing status to ADJOURNED and records the reason and next date. "
                           + "Updates case status to ADJOURNED.")
    public ResponseEntity<ApiResponse<JudgeHearingResponse>> adjournHearing(
            @PathVariable String hearingUuid,
            @Valid @RequestBody AdjournHearingRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Hearing adjourned",
                hearingService.adjournHearing(hearingUuid, request)));
    }

    @PutMapping("/judge/hearings/{hearingUuid}/complete")
    @Operation(summary = "Complete a hearing",
               description = "Marks the hearing as COMPLETED. Records actual end time.")
    public ResponseEntity<ApiResponse<JudgeHearingResponse>> completeHearing(
            @PathVariable String hearingUuid) {
        return ResponseEntity.ok(ApiResponse.success("Hearing completed",
                hearingService.completeHearing(hearingUuid)));
    }
}
