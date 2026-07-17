package com.courtai.reports.service.impl;

import com.courtai.reports.dto.request.ExportRequest;
import com.courtai.reports.dto.response.ExportResponse;
import com.courtai.reports.dto.response.ResearchDatasetRow;
import com.courtai.reports.service.ExportService;
import com.courtai.reports.service.ResearchAnalyticsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Implementation of ExportService providing JSON, CSV, and EXCEL generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ObjectMapper objectMapper;
    private final ResearchAnalyticsService researchAnalyticsService;

    @Override
    public ExportResponse exportToJson(List<?> data, String reportType) {
        try {
            String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            byte[] bytes = jsonContent.getBytes();
            
            return ExportResponse.builder()
                    .fileName(reportType + "_" + System.currentTimeMillis() + ".json")
                    .format("JSON")
                    .mimeType("application/json")
                    .recordCount(data.size())
                    .fileSizeBytes(bytes.length)
                    .generatedAt(LocalDateTime.now())
                    .base64Content(Base64.getEncoder().encodeToString(bytes))
                    .reportType(reportType)
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Failed to generate JSON export", e);
            throw new RuntimeException("Export failed", e);
        }
    }

    @Override
    public ExportResponse exportToCsv(List<ResearchDatasetRow> data, String fileName) {
        StringBuilder sb = new StringBuilder();
        // Headers
        sb.append("CaseUUID,CaseNumber,Type,Status,Priority,FilingYear,PriorityScore,UrgencyScore,DelayScore,TrustScore,ConfidenceScore,DocsVerified,DocRate,EvidenceVerified,EvidenceRate,DuplicateChecked,AgeInDays,HearingCount,CourtCode,State\n");
        
        for (ResearchDatasetRow r : data) {
            sb.append(escapeCsv(r.getCaseUuid())).append(",")
              .append(escapeCsv(r.getCaseNumber())).append(",")
              .append(r.getCaseType()).append(",")
              .append(r.getStatus()).append(",")
              .append(r.getPriority()).append(",")
              .append(r.getFilingYear()).append(",")
              .append(r.getPriorityScore()).append(",")
              .append(r.getUrgencyScore()).append(",")
              .append(r.getDelayImpactScore()).append(",")
              .append(r.getTrustScore()).append(",")
              .append(r.getConfidenceScore()).append(",")
              .append(r.getVerifiedDocuments()).append(",")
              .append(r.getDocumentCompletenessRate()).append(",")
              .append(r.getVerifiedEvidence()).append(",")
              .append(r.getEvidenceCompletenessRate()).append(",")
              .append(r.getIsDuplicateChecked()).append(",")
              .append(r.getAgeInDays()).append(",")
              .append(r.getHearingCount()).append(",")
              .append(escapeCsv(r.getCourtCode())).append(",")
              .append(escapeCsv(r.getState())).append("\n");
        }
        
        byte[] bytes = sb.toString().getBytes();
        return ExportResponse.builder()
                .fileName(fileName + ".csv")
                .format("CSV")
                .mimeType("text/csv")
                .recordCount(data.size())
                .fileSizeBytes(bytes.length)
                .generatedAt(LocalDateTime.now())
                .base64Content(Base64.getEncoder().encodeToString(bytes))
                .reportType("RESEARCH_DATASET")
                .build();
    }

    @Override
    public ExportResponse exportToExcel(List<ResearchDatasetRow> data, String sheetName, String fileName) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Dataset");
            Row headerRow = sheet.createRow(0);
            
            String[] headers = {"CaseUUID", "CaseNumber", "Type", "Status", "Priority", "PriorityScore", "TrustScore", "ConfidenceScore"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            int rowIdx = 1;
            for (ResearchDatasetRow r : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getCaseUuid() != null ? r.getCaseUuid() : "");
                row.createCell(1).setCellValue(r.getCaseNumber() != null ? r.getCaseNumber() : "");
                row.createCell(2).setCellValue(r.getCaseType() != null ? r.getCaseType() : "");
                row.createCell(3).setCellValue(r.getStatus() != null ? r.getStatus() : "");
                row.createCell(4).setCellValue(r.getPriority() != null ? r.getPriority() : "");
                row.createCell(5).setCellValue(r.getPriorityScore() != null ? r.getPriorityScore() : 0.0);
                row.createCell(6).setCellValue(r.getTrustScore() != null ? r.getTrustScore() : 0.0);
                row.createCell(7).setCellValue(r.getConfidenceScore() != null ? r.getConfidenceScore() : 0.0);
            }
            
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            
            return ExportResponse.builder()
                    .fileName(fileName + ".xlsx")
                    .format("EXCEL")
                    .mimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .recordCount(data.size())
                    .fileSizeBytes(bytes.length)
                    .generatedAt(LocalDateTime.now())
                    .base64Content(Base64.getEncoder().encodeToString(bytes))
                    .reportType("RESEARCH_DATASET")
                    .build();
                    
        } catch (IOException e) {
            log.error("Failed to generate Excel export", e);
            throw new RuntimeException("Excel export failed", e);
        }
    }

    @Override
    public ExportResponse exportCasesToCsv(List<?> cases, String fileName) {
        return exportToJson(cases, "CASES_FALLBACK");
    }

    @Override
    public ExportResponse processExportRequest(ExportRequest request) {
        String reportType = request.getReportType();
        String format = request.getFormat() != null ? request.getFormat().toUpperCase() : "JSON";
        String name = reportType.toLowerCase() + "_" + System.currentTimeMillis();
        
        if ("RESEARCH_DATASET".equals(reportType)) {
            List<ResearchDatasetRow> data = researchAnalyticsService.generateFullDataset();
            
            if ("CSV".equals(format)) {
                return exportToCsv(data, name);
            } else if ("EXCEL".equals(format)) {
                return exportToExcel(data, request.getSheetName(), name);
            } else {
                return exportToJson(data, reportType);
            }
        }
        
        // Fallback for other report types
        return exportToJson(List.of("Export for " + reportType + " not fully implemented"), reportType);
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
