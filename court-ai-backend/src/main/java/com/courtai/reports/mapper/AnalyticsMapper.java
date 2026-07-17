package com.courtai.reports.mapper;

import com.courtai.reports.dto.response.GraphDataPoint;
import com.courtai.reports.dto.response.TimeSeriesDataPoint;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utility mapper for converting JPQL projection arrays to analytics DTOs.
 *
 * <p>JPQL group-by queries return {@code Object[]} arrays. This mapper
 * converts them into typed {@link GraphDataPoint} and {@link TimeSeriesDataPoint}
 * objects for clean service → controller handoff.</p>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AnalyticsMapper {

    /**
     * Converts a list of [label, count] Object[] rows to GraphDataPoint list.
     *
     * @param rows  list of Object[2]: {label (Object), value (Number)}
     * @return      flat graph data list with percentage filled
     */
    public List<GraphDataPoint> toGraphDataPoints(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) return List.of();

        double total = rows.stream()
                .mapToDouble(r -> r[1] == null ? 0 : ((Number) r[1]).doubleValue())
                .sum();

        List<GraphDataPoint> result = new ArrayList<>();
        for (Object[] row : rows) {
            String label = row[0] == null ? "Unknown" : row[0].toString();
            double value = row[1] == null ? 0 : ((Number) row[1]).doubleValue();
            double pct   = total > 0 ? (value / total * 100) : 0;
            result.add(GraphDataPoint.builder()
                    .label(label)
                    .value(value)
                    .percentage(Math.round(pct * 100.0) / 100.0)
                    .build());
        }
        return result;
    }

    /**
     * Converts [year, month, count] Object[] rows to TimeSeriesDataPoint list.
     */
    public List<TimeSeriesDataPoint> toMonthlyTimeSeries(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) return List.of();

        List<TimeSeriesDataPoint> result = new ArrayList<>();
        for (Object[] row : rows) {
            int year  = row[0] == null ? 0 : ((Number) row[0]).intValue();
            int month = row[1] == null ? 0 : ((Number) row[1]).intValue();
            long count = row[2] == null ? 0 : ((Number) row[2]).longValue();
            String label = month > 0
                    ? Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year
                    : String.valueOf(year);
            result.add(TimeSeriesDataPoint.builder()
                    .year(year)
                    .month(month)
                    .date(month > 0 ? LocalDate.of(year, month, 1) : null)
                    .label(label)
                    .count(count)
                    .build());
        }
        return result;
    }

    /**
     * Converts [year, month, value (Double)] Object[] rows to TimeSeriesDataPoint list.
     */
    public List<TimeSeriesDataPoint> toMonthlyValueTimeSeries(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) return List.of();

        List<TimeSeriesDataPoint> result = new ArrayList<>();
        for (Object[] row : rows) {
            int year  = row[0] == null ? 0 : ((Number) row[0]).intValue();
            int month = row[1] == null ? 0 : ((Number) row[1]).intValue();
            double value = row[2] == null ? 0 : ((Number) row[2]).doubleValue();
            String label = month > 0
                    ? Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year
                    : String.valueOf(year);
            result.add(TimeSeriesDataPoint.builder()
                    .year(year)
                    .month(month)
                    .date(month > 0 ? LocalDate.of(year, month, 1) : null)
                    .label(label)
                    .value(Math.round(value * 100.0) / 100.0)
                    .build());
        }
        return result;
    }

    /**
     * Fills in months with no data as zeros for a complete 12-month trend.
     */
    public List<TimeSeriesDataPoint> fillMissingMonths(List<TimeSeriesDataPoint> series, int year) {
        List<TimeSeriesDataPoint> complete = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            final int month = m;
            TimeSeriesDataPoint existing = series.stream()
                    .filter(p -> p.getYear() != null && p.getYear() == year
                              && p.getMonth() != null && p.getMonth() == month)
                    .findFirst()
                    .orElse(null);
            if (existing != null) {
                complete.add(existing);
            } else {
                String label = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
                complete.add(TimeSeriesDataPoint.builder()
                        .year(year).month(m)
                        .date(LocalDate.of(year, m, 1))
                        .label(label).count(0L).value(0.0)
                        .build());
            }
        }
        return complete;
    }
}
