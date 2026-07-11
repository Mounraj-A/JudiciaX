package com.courtai.clerk.service;

import com.courtai.clerk.dto.EvidenceVerificationResponse;
import com.courtai.clerk.dto.VerifyEvidenceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service contract for clerk evidence verification. */
public interface ClerkEvidenceVerificationService {

    /** List all evidence for a case. */
    Page<EvidenceVerificationResponse> getEvidence(String caseUuid, Pageable pageable);

    /** Get a single evidence detail. */
    EvidenceVerificationResponse getEvidenceByUuid(String caseUuid, String evidenceUuid);

    /** Verify or reject an evidence item. */
    EvidenceVerificationResponse verifyEvidence(String caseUuid, String evidenceUuid,
                                                VerifyEvidenceRequest request);
}
