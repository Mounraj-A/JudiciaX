package com.courtai.judge.dto;

import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request payload for updating an existing hearing's mutable fields.
 */
@Getter
@NoArgsConstructor
public class UpdateHearingRequest {

    @Future(message = "Rescheduled hearing must be in the future")
    private LocalDateTime scheduledAt;

    private String courtRoomUuid;
    private String notes;
    private Boolean isVirtual;
}
