package com.courtai.advocate.service;

import com.courtai.advocate.dto.CreateEvidenceRequest;
import com.courtai.advocate.dto.EvidenceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for advocate evidence management.
 */
public interface AdvocateEvidenceService {

    /** Submit new evidence for a case. */
    EvidenceResponse submitEvidence(String caseUuid, CreateEvidenceRequest request);

    /** Returns all evidence for a case. */
    Page<EvidenceResponse> getEvidence(String caseUuid, Pageable pageable);

    /** Returns a specific evidence item. */
    EvidenceResponse getEvidenceByUuid(String caseUuid, String evidenceUuid);

    /**
     * Deletes evidence — only allowed before it is court-admitted.
     */
    void deleteEvidence(String caseUuid, String evidenceUuid);
}
