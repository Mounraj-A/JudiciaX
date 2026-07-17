package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response returned by export endpoints describing the generated export file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse {

    /** Name of the generated file (e.g., "research_dataset_2025.csv"). */
    private String fileName;

    /** Export format: JSON, CSV, EXCEL. */
    private String format;

    /** MIME type of the generated file. */
    private String mimeType;

    /** Number of data records exported. */
    private long recordCount;

    /** File size in bytes. */
    private long fileSizeBytes;

    /** Timestamp when the export was generated. */
    private LocalDateTime generatedAt;

    /** Base64-encoded file content for small exports (null for large files). */
    private String base64Content;

    /** Report type exported. */
    private String reportType;
}
