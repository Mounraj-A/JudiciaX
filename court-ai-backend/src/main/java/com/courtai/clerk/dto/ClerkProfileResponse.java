package com.courtai.clerk.dto;

import lombok.Builder;
import lombok.Data;

/** Clerk profile information. */
@Data
@Builder
public class ClerkProfileResponse {
    private String uuid;
    private String fullName;
    private String email;
    private String employeeId;
    private String courtSection;
    private String department;
    private String courtUuid;
    private String courtName;
    private String courtCode;
}
