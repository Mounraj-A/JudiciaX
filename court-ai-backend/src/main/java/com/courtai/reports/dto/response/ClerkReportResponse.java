package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Clerk performance analytics report response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClerkReportResponse {

    private String clerkUuid;
    private String clerkName;
    private String employeeId;
    private String courtName;
    private String courtSection;
    private String department;

    // ── Case Processing Counts ────────────────────────────────────────────

    private long casesUnderScrutiny;
    private long registeredCases;
    private long returnedCases;
    private long rejectedCases;
    private long totalProcessed;

    // ── Timing Metrics ────────────────────────────────────────────────────

    /** Average hours from case FILED to REGISTERED (scrutiny time). */
    private Double avgScrutinyHours;

    // ── Quality Metrics ───────────────────────────────────────────────────

    /** Number of cases where duplicates were detected. */
    private long duplicateDetectionCount;

    /** Number of cases where jurisdiction was verified. */
    private long jurisdictionVerifiedCount;

    /**
     * Verification accuracy rate — proportion of cases registered on first scrutiny
     * (not returned or rejected after registration).
     */
    private Double verificationAccuracy;

    // ── Charts ────────────────────────────────────────────────────────────

    /** Cases by processing outcome. */
    private List<GraphDataPoint> outcomeDistribution;

    /** Monthly registered cases trend. */
    private List<TimeSeriesDataPoint> monthlyRegistrationTrend;
}
