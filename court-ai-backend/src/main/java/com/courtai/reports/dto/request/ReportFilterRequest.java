package com.courtai.reports.dto.request;

import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Universal filter request for all report and analytics endpoints.
 *
 * <p>All fields are optional. Null values are ignored in query building.
 * Date range: if only {@code fromDate} is provided, queries from that date to now.
 * If only {@code toDate} is provided, queries from epoch to that date.</p>
 */
@Data
@NoArgsConstructor
public class ReportFilterRequest {

    // ── Date Range ────────────────────────────────────────────────────────

    /** Start date (inclusive). */
    private LocalDate fromDate;

    /** End date (inclusive). */
    private LocalDate toDate;

    /** Filter by year (e.g., 2025). */
    private Integer year;

    /** Filter by month (1–12). */
    private Integer month;

    // ── Location ─────────────────────────────────────────────────────────

    /** Court ID (surrogate key). */
    private Long courtId;

    /** State name. */
    private String state;

    /** District name. */
    private String district;

    // ── Person Filters ────────────────────────────────────────────────────

    /** Judge UUID. */
    private String judgeUuid;

    /** Advocate UUID. */
    private String advocateUuid;

    /** Clerk UUID. */
    private String clerkUuid;

    // ── Case Filters ──────────────────────────────────────────────────────

    /** Filter by case type. */
    private CaseType caseType;

    /** Filter by case status. */
    private CaseStatus caseStatus;

    /** Filter by case priority. */
    private CasePriority priority;

    /** Filter by case category UUID. */
    private String categoryUuid;

    // ── AI Filters ────────────────────────────────────────────────────────

    /** Minimum AI confidence score (0–100). */
    private Double minAiConfidence;

    /** Maximum AI confidence score (0–100). */
    private Double maxAiConfidence;

    /** Minimum trust score (0–100). */
    private Double minTrustScore;

    /** Maximum trust score (0–100). */
    private Double maxTrustScore;

    // ── Document / Evidence Filters ───────────────────────────────────────

    /** Filter by duplicate flag status. */
    private Boolean isDuplicateChecked;

    /** Filter by jurisdiction verification. */
    private Boolean isJurisdictionVerified;

    // ── Pagination ────────────────────────────────────────────────────────

    /** Page number (0-indexed). Defaults to 0. */
    private int page = 0;

    /** Page size. Defaults to 20, max 500. */
    private int size = 20;
}
