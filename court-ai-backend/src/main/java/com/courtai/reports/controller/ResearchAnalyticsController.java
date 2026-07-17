package com.courtai.reports.controller;

import com.courtai.common.dto.ApiResponse;
import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.ResearchAnalyticsResponse;
import com.courtai.reports.dto.response.ResearchDatasetRow;
import com.courtai.reports.service.ResearchAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Research Dataset Generation and Analysis.
 *
 * <p>This is the primary endpoint group for exporting and reviewing data
 * intended for the TrustCourt research paper evaluation phases.</p>
 */
@RestController
@RequestMapping("/api/reports/research")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Reports: Research", description = "Research dataset and explainability analysis APIs")
public class ResearchAnalyticsController {

    private final ResearchAnalyticsService researchAnalyticsService;

    @GetMapping("/dataset")
    @Operation(summary = "Get paginated research dataset", description = "Returns the flat dataset required for research evaluation.")
    public ResponseEntity<ApiResponse<Page<ResearchDatasetRow>>> getResearchDataset(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Research dataset fetched successfully",
                researchAnalyticsService.getResearchDataset(pageable)));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get research metrics summary", description = "Aggregated statistics over the research dataset.")
    public ResponseEntity<ApiResponse<ResearchAnalyticsResponse>> getResearchSummary() {
        return ResponseEntity.ok(ApiResponse.success(
                "Research summary fetched successfully",
                researchAnalyticsService.getResearchSummary()));
    }

    @GetMapping("/explainability")
    @Operation(summary = "Get AI explainability report", description = "Distributions of confidence, urgency, and impact scores.")
    public ResponseEntity<ApiResponse<ResearchAnalyticsResponse>> getExplainabilityReport() {
        return ResponseEntity.ok(ApiResponse.success(
                "Explainability report fetched successfully",
                researchAnalyticsService.getExplainabilityReport()));
    }

    @GetMapping("/feature-correlation")
    @Operation(summary = "Get feature correlation matrix", description = "Relationships between AI scores and lifecycle metrics.")
    public ResponseEntity<ApiResponse<List<GraphDataPoint>>> getFeatureCorrelationSummary() {
        return ResponseEntity.ok(ApiResponse.success(
                "Feature correlation fetched successfully",
                researchAnalyticsService.getFeatureCorrelationSummary()));
    }
}
