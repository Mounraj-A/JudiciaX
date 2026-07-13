package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CourtRoomResponse {
    private String uuid;
    private String courtUuid;
    private String courtName;
    private String roomNumber;
    private String floor;
    private Integer capacity;
    private Boolean hasVideoConferencing;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
