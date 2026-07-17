package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Notification system analytics response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAnalyticsResponse {

    // ── Summary ───────────────────────────────────────────────────────────

    private long totalNotifications;
    private long totalDeliveries;
    private long delivered;
    private long failed;
    private long pending;
    private long read;

    /** Delivery success rate (0–100). */
    private Double deliveryRate;

    /** Read rate among delivered notifications (0–100). */
    private Double readRate;

    // ── Breakdowns ────────────────────────────────────────────────────────

    /** Notification counts by type (HEARING_REMINDER, CASE_UPDATE, etc.). */
    private List<GraphDataPoint> byType;

    /** Notification counts by priority (HIGH, NORMAL, LOW). */
    private List<GraphDataPoint> byPriority;

    /** Delivery rate by notification type. */
    private List<GraphDataPoint> deliveryRateByType;

    // ── Trends ────────────────────────────────────────────────────────────

    /** Monthly notification volume trend. */
    private List<TimeSeriesDataPoint> deliveryTrend;

    /** Monthly delivery failure trend. */
    private List<TimeSeriesDataPoint> failureTrend;
}
