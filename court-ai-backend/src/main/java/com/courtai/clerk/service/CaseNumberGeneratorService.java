package com.courtai.clerk.service;

import com.courtai.casefile.entity.CaseFile;

/**
 * Service contract for generating official court case numbers.
 *
 * <p>Format: {@code STATE-DISTRICT-COURT_CODE-YEAR-SEQUENCE}
 * e.g. {@code TN-COIMBATORE-DC-2026-000001}</p>
 *
 * <p>The sequence is per-court per-year and increments atomically
 * via a pessimistic-write database lock.</p>
 */
public interface CaseNumberGeneratorService {

    /**
     * Generates and returns the next official case number for the given case.
     * This operation is transactional and uses a DB-level row lock.
     *
     * @param caseFile the case for which to generate the number
     * @return formatted official case number string
     */
    String generateOfficialCaseNumber(CaseFile caseFile);
}
