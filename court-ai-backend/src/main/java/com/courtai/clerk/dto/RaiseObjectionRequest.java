package com.courtai.clerk.dto;

import com.courtai.common.enums.ObjectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Request body for clerk to raise a formal objection on a case. */
@Data
public class RaiseObjectionRequest {

    @NotNull(message = "Objection type is required")
    private ObjectionType objectionType;

    @NotBlank(message = "Reason is required")
    private String reason;

    /** Comma-separated names of missing documents (when objectionType = MISSING_DOCUMENT). */
    private String missingDocuments;

    /** Description of corrections the advocate must make. */
    private String correctionRequired;
}
