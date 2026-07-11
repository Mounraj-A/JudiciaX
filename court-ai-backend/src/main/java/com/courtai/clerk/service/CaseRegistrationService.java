package com.courtai.clerk.service;

import com.courtai.clerk.dto.CaseRegistrationResponse;

/**
 * Service contract for official case registration.
 *
 * <p>Registration is the final scrutiny step. After registration:
 * <ol>
 *   <li>Status moves to {@code REGISTERED}</li>
 *   <li>Official case number is generated</li>
 *   <li>Case is placed in the judge assignment queue</li>
 *   <li>Advocate is notified</li>
 * </ol>
 * </p>
 */
public interface CaseRegistrationService {

    /**
     * Registers a case — runs duplicate check, jurisdiction check,
     * generates the official case number, updates status to REGISTERED,
     * places the case in the judge queue, and notifies the advocate.
     *
     * @param caseUuid the UUID of the case to register
     * @param remarks  clerk remarks recorded at registration
     */
    CaseRegistrationResponse registerCase(String caseUuid, String remarks);
}
