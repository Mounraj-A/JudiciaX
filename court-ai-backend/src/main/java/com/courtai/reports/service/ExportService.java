package com.courtai.reports.service;

import com.courtai.reports.dto.request.ExportRequest;
import com.courtai.reports.dto.response.ExportResponse;
import com.courtai.reports.dto.response.ResearchDatasetRow;

import java.util.List;

/**
 * Export service — converts analytics data into CSV, Excel, or JSON.
 */
public interface ExportService {

    ExportResponse exportToJson(List<?> data, String reportType);

    ExportResponse exportToCsv(List<ResearchDatasetRow> data, String fileName);

    ExportResponse exportToExcel(List<ResearchDatasetRow> data, String sheetName, String fileName);

    ExportResponse exportCasesToCsv(List<?> cases, String fileName);

    ExportResponse processExportRequest(ExportRequest request);
}
