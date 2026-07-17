package com.courtai.reports.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated wrapper for report list endpoints.
 *
 * @param <T> type of each item in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedReportResponse<T> {

    /** The page content. */
    private List<T> content;

    /** Total number of matching records across all pages. */
    private long totalElements;

    /** Total number of pages. */
    private int totalPages;

    /** Current page number (0-indexed). */
    private int page;

    /** Page size. */
    private int size;

    /** Whether this is the first page. */
    private boolean first;

    /** Whether this is the last page. */
    private boolean last;
}
