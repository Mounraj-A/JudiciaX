package com.courtai.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Request to assign a clerk to a court. */
@Getter
@NoArgsConstructor
public class AssignClerkRequest {

    @NotBlank(message = "Court UUID is required")
    private String courtUuid;

    @NotBlank(message = "Clerk user UUID is required")
    private String clerkUserUuid;

    private String reason;
}
