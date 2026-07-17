package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A single point in a time-series chart (line or area chart).
 *
 * <p>Used for monthly trends, yearly trends, growth curves, etc.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesDataPoint {

    /** Date for this data point. */
    private LocalDate date;

    /** Year (for year-level aggregation). */
    private Integer year;

    /** Month (1–12). */
    private Integer month;

    /** Label (e.g., "Jan 2025", "Q1 2025"). */
    private String label;

    /** Primary count (e.g., cases filed). */
    private Long count;

    /** Primary floating-point value (e.g., average score). */
    private Double value;

    /** Optional secondary count (e.g., cases disposed alongside cases filed). */
    private Long secondaryCount;
}
