package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Audit system analytics response — covers AuditLog, AuditEvent,
 * AuditIntegrity, and SecurityAudit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditAnalyticsResponse {

    // ── Summary Counts ────────────────────────────────────────────────────

    private long totalAuditLogs;
    private long totalAuditEvents;
    private long totalSecurityEvents;
    private long totalComplianceAudits;
    private long totalIntegrityRecords;

    // ── Integrity ─────────────────────────────────────────────────────────

    private long tamperAttempts;
    private long integrityVerifiedCount;
    private long integrityFailedCount;

    // ── Breakdown Charts ──────────────────────────────────────────────────

    /** Audit logs by module. */
    private List<GraphDataPoint> byModule;

    /** Audit logs by action type. */
    private List<GraphDataPoint> byAction;

    /** Audit logs by actor role. */
    private List<GraphDataPoint> byRole;

    /** Audit logs by outcome (SUCCESS / FAILURE). */
    private List<GraphDataPoint> byOutcome;

    // ── Growth Trend ──────────────────────────────────────────────────────

    /** Monthly audit log count trend. */
    private List<TimeSeriesDataPoint> auditGrowthTrend;

    /** Monthly security event count trend. */
    private List<TimeSeriesDataPoint> securityEventTrend;
}
