package com.courtai.judge.service;

import com.courtai.judge.dto.JudgeDocumentResponse;

import java.util.List;

/**
 * Service contract for the judge's read-only document review.
 * The judge cannot upload, modify, or delete documents.
 */
public interface JudgeDocumentService {

    /** Returns all documents for a case assigned to the current judge. */
    List<JudgeDocumentResponse> getDocumentsByCase(String caseUuid);

    /** Returns a specific document — validates judge is assigned to the case. */
    JudgeDocumentResponse getDocumentByUuid(String caseUuid, String documentUuid);
}
