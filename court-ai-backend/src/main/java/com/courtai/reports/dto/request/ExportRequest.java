package com.courtai.reports.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for report export endpoints.
 *
 * <p>Combines format selection, report type, and an optional filter
 * to control what data is exported and in which format.</p>
 */
@Data
@NoArgsConstructor
public class ExportRequest {

    /**
     * Export format.
     * Supported values: JSON, CSV, EXCEL
     */
    private String format = "JSON";

    /**
     * Type of report to export.
     * e.g., CASES, RESEARCH_DATASET, COURT, JUDGE, ADVOCATE, CLERK, PERFORMANCE
     */
    private String reportType;

    /** Optional filter criteria for the export. */
    private ReportFilterRequest filter;

    /** Sheet name for Excel exports. Defaults to report type. */
    private String sheetName;

    /** Maximum records to export (safety cap). Defaults to 10,000. */
    private int maxRecords = 10_000;
}
