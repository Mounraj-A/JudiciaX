package com.courtai.judge.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request payload for scheduling a new court hearing.
 */
@Getter
@NoArgsConstructor
public class ScheduleHearingRequest {

    @NotNull(message = "Case UUID is required")
    private String caseUuid;

    @NotNull(message = "Scheduled date/time is required")
    @Future(message = "Hearing must be scheduled in the future")
    private LocalDateTime scheduledAt;

    /** UUID of the courtroom where the hearing will be held. */
    private String courtRoomUuid;

    /** Notes or agenda for this hearing session. */
    private String notes;

    /** Whether this hearing will be conducted via video conferencing. */
    private Boolean isVirtual;
}
