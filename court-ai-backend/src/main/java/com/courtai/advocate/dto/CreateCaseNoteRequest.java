package com.courtai.advocate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCaseNoteRequest {
    @NotBlank(message = "Note title cannot be blank")
    private String noteTitle;

    @NotBlank(message = "Note content cannot be blank")
    private String noteContent;
}
