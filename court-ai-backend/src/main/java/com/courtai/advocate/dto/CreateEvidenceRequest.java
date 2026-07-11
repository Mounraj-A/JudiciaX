package com.courtai.advocate.dto;

import com.courtai.common.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for submitting evidence to a case.
 * File upload is handled separately via multipart — this carries the metadata.
 */
@Getter
@Setter
public class CreateEvidenceRequest {

    @NotNull(message = "Evidence type is required")
    private EvidenceType evidenceType;

    @NotBlank(message = "Evidence title is required")
    @Size(max = 300)
    private String title;

    @Size(max = 5000)
    private String description;

    @Size(max = 200)
    private String collectedBy;

    private LocalDate collectedAt;

    @Size(max = 500)
    private String location;

    /** Optional UUID of an already-uploaded Document to link to this evidence. */
    private String documentUuid;
}
