package com.courtai.clerk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Request body for document verification or rejection by clerk. */
@Data
public class VerifyDocumentRequest {

    /** Whether the document is approved (true) or rejected (false). */
    private boolean approved;

    /** Clerk's remarks — required for rejection, optional for approval. */
    @NotBlank(message = "Remarks are required")
    private String remarks;

    /** Reason for rejection — required when approved=false. */
    private String rejectionReason;
}
