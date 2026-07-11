package com.courtai.advocate.service;

import com.courtai.advocate.dto.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service contract for advocate document management.
 * Upload, list, download, and delete are all scoped to the advocate's own cases.
 */
public interface AdvocateDocumentService {

    /**
     * Uploads a document for the given case.
     *
     * @param caseUuid    the UUID of the case
     * @param file        the multipart file
     * @param documentType the document type string (e.g., "PETITION")
     * @param description optional description
     */
    DocumentResponse uploadDocument(String caseUuid, MultipartFile file,
                                    String documentType, String description);

    /** Returns all documents for a case — validates ownership. */
    Page<DocumentResponse> getDocuments(String caseUuid, Pageable pageable);

    /** Returns a document's metadata — validates ownership. */
    DocumentResponse getDocument(String caseUuid, String documentUuid);

    /**
     * Deletes a document — only allowed before the case is verified.
     * Also validates advocate ownership of both case and document.
     */
    void deleteDocument(String caseUuid, String documentUuid);
}
