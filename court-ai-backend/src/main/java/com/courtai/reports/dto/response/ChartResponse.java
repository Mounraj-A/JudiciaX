package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Chart-ready response wrapping a list of {@link GraphDataPoint}s.
 *
 * <p>The {@code chartType} field hints to the frontend which chart to render
 * (e.g., BAR, PIE, LINE, AREA, DOUGHNUT, HEATMAP).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponse {

    /** Suggested chart type: BAR, PIE, LINE, AREA, DOUGHNUT, HEATMAP. */
    private String chartType;

    /** Human-readable title for the chart. */
    private String title;

    /** X-axis label. */
    private String xAxisLabel;

    /** Y-axis label. */
    private String yAxisLabel;

    /** The data points. */
    private List<GraphDataPoint> data;

    /** Total for percentage calculation. */
    private Double total;
}
