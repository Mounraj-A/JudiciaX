package com.courtai.clerk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Request body for PUT verify and PUT return actions. */
@Data
public class ScrutinyActionRequest {

    /** Clerk's remarks on the scrutiny action. */
    @NotBlank(message = "Remarks are required")
    private String remarks;
}
