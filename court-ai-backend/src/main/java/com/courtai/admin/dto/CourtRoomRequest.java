package com.courtai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourtRoomRequest {
    @NotBlank
    private String courtUuid;
    @NotBlank
    private String roomNumber;
    private String floor;
    private Integer capacity;
    private Boolean hasVideoConferencing;
    private Boolean isActive;
}
