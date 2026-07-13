package com.courtai.casemanagement.service;

import com.courtai.casemanagement.dto.CaseSummaryResponse;
import com.courtai.casemanagement.dto.CaseSearchRequest;
import org.springframework.data.domain.Page;

/** Full-text and faceted search across all case dimensions. */
public interface CaseSearchService {

    /**
     * Executes an advanced multi-dimensional case search.
     * All filter fields in {@link CaseSearchRequest} are optional and combined with AND logic.
     *
     * @param request the search criteria (all fields optional)
     * @return paginated, sorted list of matching cases
     */
    Page<CaseSummaryResponse> search(CaseSearchRequest request);
}
