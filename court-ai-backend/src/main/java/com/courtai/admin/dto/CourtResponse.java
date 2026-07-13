package com.courtai.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** Court response returned by admin court endpoints. */
@Getter
@Builder
public class CourtResponse {
    private String uuid;
    private String courtCode;
    private String courtName;
    private String courtType;
    private String state;
    private String district;
    private String address;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
