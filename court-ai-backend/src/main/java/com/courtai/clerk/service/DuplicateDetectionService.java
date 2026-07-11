package com.courtai.clerk.service;

import com.courtai.clerk.dto.DuplicateCheckResponse;

/**
 * Service contract for duplicate case detection.
 *
 * <p>Compares petitioner, respondent, case type, and court.
 * Does NOT automatically reject — the clerk makes the decision.</p>
 */
public interface DuplicateDetectionService {

    /**
     * Checks whether a case is a potential duplicate of an existing case.
     * Marks the case as duplicate-checked and stores potential UUID list.
     *
     * @param caseUuid the UUID of the case to check
     */
    DuplicateCheckResponse checkForDuplicates(String caseUuid);
}
