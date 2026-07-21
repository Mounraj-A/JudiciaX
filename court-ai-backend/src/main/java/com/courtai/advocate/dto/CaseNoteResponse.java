package com.courtai.advocate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseNoteResponse {
    private String uuid;
    private String caseUuid;
    private String noteTitle;
    private String noteContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
