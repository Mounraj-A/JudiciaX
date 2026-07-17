package com.courtai.reports.service;

import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.ResearchAnalyticsResponse;
import com.courtai.reports.dto.response.ResearchDatasetRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Research analytics service — primary data source for the TrustCourt research paper.
 */
public interface ResearchAnalyticsService {

    /** Paginated research dataset — each row is one case with all features. */
    Page<ResearchDatasetRow> getResearchDataset(Pageable pageable);

    /** Full (unpaginated) dataset for export. */
    List<ResearchDatasetRow> generateFullDataset();

    /** Aggregate research metrics summary. */
    ResearchAnalyticsResponse getResearchSummary();

    /** Explainability analytics — AI score distributions and averages. */
    ResearchAnalyticsResponse getExplainabilityReport();

    /** Priority accuracy — AI priority vs final case disposition. */
    List<GraphDataPoint> getPriorityAccuracyReport();

    /** Trust analysis breakdown. */
    List<GraphDataPoint> getTrustAnalysisBreakdown();

    /** Feature importance correlation data (computed from averages). */
    List<GraphDataPoint> getFeatureCorrelationSummary();
}
