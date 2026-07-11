package com.courtai.clerk.service;

import com.courtai.clerk.dto.DocumentVerificationResponse;
import com.courtai.clerk.dto.VerifyDocumentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service contract for clerk document verification. */
public interface ClerkDocumentVerificationService {

    /** List all documents for a case. */
    Page<DocumentVerificationResponse> getDocuments(String caseUuid, Pageable pageable);

    /** Get a single document detail. */
    DocumentVerificationResponse getDocument(String caseUuid, String documentUuid);

    /** Verify or reject a document. */
    DocumentVerificationResponse verifyDocument(String caseUuid, String documentUuid,
                                                VerifyDocumentRequest request);
}
