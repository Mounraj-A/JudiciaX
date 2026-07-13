package com.courtai.casemanagement.service;

import com.courtai.casemanagement.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Central case lifecycle management service.
 *
 * <p>Orchestrates CRUD, lifecycle actions (archive, cancel, clone, etc.),
 * delegating status transitions to {@link CaseWorkflowService}.</p>
 */
public interface CaseManagementService {

    /** Create a new case in DRAFT status. */
    CaseDetailsResponse createCase(CaseCreateRequest request, String actorUuid, String actorRole);

    /** Update an editable case (DRAFT only for advocates; admin may update more fields). */
    CaseDetailsResponse updateCase(String caseUuid, CaseUpdateRequest request,
                                   String actorUuid, String actorRole);

    /** Get full case details by UUID. */
    CaseDetailsResponse getCase(String caseUuid);

    /** List cases with pagination. */
    Page<CaseSummaryResponse> listCases(Pageable pageable);

    /** Submit a DRAFT case for court scrutiny. */
    CaseDetailsResponse submitCase(String caseUuid, String actorUuid, String actorRole);

    /** Archive a DISPOSED or CLOSED case (makes it read-only). */
    CaseDetailsResponse archiveCase(String caseUuid, CaseArchiveRequest request,
                                    String actorUuid, String actorRole);

    /** Close a DISPOSED case. */
    CaseDetailsResponse closeCase(String caseUuid, String actorUuid, String actorRole, String reason);

    /** Reopen a CLOSED case (admin only). */
    CaseDetailsResponse reopenCase(String caseUuid, String actorUuid, String actorRole, String reason);

    /** Cancel a case before registration (advocate/admin). */
    CaseDetailsResponse cancelCase(String caseUuid, CaseCancelRequest request,
                                   String actorUuid, String actorRole);

    /**
     * Clone a DRAFT or SUBMITTED case — copies basic details and parties.
     * Does NOT copy evidence, documents, hearings, judge notes, or AI data.
     */
    CaseCloneResponse cloneCase(String caseUuid, CaseCloneRequest request,
                                String actorUuid, String actorRole);

    /** Soft-delete a DRAFT case (advocate/admin only). */
    void deleteCase(String caseUuid, String actorUuid, String actorRole);
}
