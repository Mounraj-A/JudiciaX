package com.courtai.judge.service;

import com.courtai.judge.dto.AdjournHearingRequest;
import com.courtai.judge.dto.JudgeHearingResponse;
import com.courtai.judge.dto.ScheduleHearingRequest;
import com.courtai.judge.dto.UpdateHearingRequest;

import java.util.List;

/**
 * Service contract for judge hearing management.
 */
public interface JudgeHearingService {

    /** Returns all hearings (across all cases) for the current judge. */
    List<JudgeHearingResponse> getMyHearings();

    /** Returns a specific hearing by UUID. Validates judge ownership. */
    JudgeHearingResponse getHearingByUuid(String hearingUuid);

    /**
     * Schedules a new hearing for a case.
     * Validates:
     * <ul>
     *   <li>Case is assigned to the current judge</li>
     *   <li>Case is not DISPOSED</li>
     *   <li>No duplicate hearing at the same time and courtroom</li>
     * </ul>
     */
    JudgeHearingResponse scheduleHearing(ScheduleHearingRequest request);

    /** Updates mutable fields of an existing hearing. */
    JudgeHearingResponse updateHearing(String hearingUuid, UpdateHearingRequest request);

    /**
     * Adjourns a hearing — sets status to ADJOURNED, stores reason and next date.
     * Updates the case status to ADJOURNED.
     */
    JudgeHearingResponse adjournHearing(String hearingUuid, AdjournHearingRequest request);

    /**
     * Marks a hearing as COMPLETED.
     * Records actual start/end timestamps.
     */
    JudgeHearingResponse completeHearing(String hearingUuid);
}
