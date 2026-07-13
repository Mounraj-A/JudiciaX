package com.courtai.casemanagement.service;

import com.courtai.casemanagement.dto.CaseHistoryResponse;

import java.util.List;

/** Service for retrieving the immutable status-change history of a case. */
public interface CaseHistoryService {

    /**
     * Returns the full status transition history of a case, ordered oldest → newest.
     */
    List<CaseHistoryResponse> getStatusHistory(String caseUuid);

    /**
     * Returns the assignment history (judge assignments) for a case.
     */
    List<CaseHistoryResponse> getAssignmentHistory(String caseUuid);
}
