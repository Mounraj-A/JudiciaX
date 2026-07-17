package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single data point used in bar, pie, and line charts.
 *
 * <p>Used uniformly across all analytics endpoints to allow
 * the frontend to render any chart type from a flat list.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphDataPoint {

    /** Human-readable label for this point (e.g., court name, status name, month). */
    private String label;

    /** Primary numeric value (count, average, etc.). */
    private Double value;

    /** Percentage of the total (0–100), if applicable. */
    private Double percentage;

    /** Optional secondary value (e.g., disposed count alongside filed count). */
    private Double secondaryValue;

    /** Optional label for the secondary value. */
    private String secondaryLabel;

    /** Optional colour hint for the chart (hex or CSS colour name). */
    private String color;
}
