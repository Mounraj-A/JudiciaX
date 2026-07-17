package com.courtai.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditExportRequest {
    private String format; // PDF, EXCEL, CSV
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String module;
    private String role;
}
