package com.courtai.clerk.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseStatusHistory;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casefile.repository.CaseStatusHistoryRepository;
import com.courtai.clerk.dto.*;
import com.courtai.clerk.entity.CaseObjection;
import com.courtai.clerk.mapper.ClerkCaseMapper;
import com.courtai.clerk.mapper.ClerkObjectionMapper;
import com.courtai.clerk.repository.CaseObjectionRepository;
import com.courtai.clerk.service.ClerkCaseService;
import com.courtai.clerk.util.ClerkNotificationHelper;
import com.courtai.clerk.util.ClerkSecurityUtil;
import com.courtai.common.enums.CaseStatus;
import com.courtai.document.repository.DocumentRepository;
import com.courtai.evidence.repository.EvidenceRepository;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ClerkCaseService}.
 *
 * <p>All operations enforce the clerk's court scope via {@link ClerkSecurityUtil}.</p>
 *
 * <h3>Allowed status transitions:</h3>
 * <pre>
 * SUBMITTED     → UNDER_SCRUTINY  (openScrutiny)
 * UNDER_SCRUTINY → RETURNED       (returnCase)
 * UNDER_SCRUTINY → REGISTERED     (via CaseRegistrationService)
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClerkCaseServiceImpl implements ClerkCaseService {

    private static final Set<CaseStatus> PENDING_STATUSES =
            Set.of(CaseStatus.SUBMITTED, CaseStatus.UNDER_SCRUTINY, CaseStatus.RETURNED);

    private final ClerkSecurityUtil           securityUtil;
    private final CaseFileRepository          caseFileRepository;
    private final CaseStatusHistoryRepository statusHistoryRepository;
    private final CaseObjectionRepository     objectionRepository;
    private final DocumentRepository          documentRepository;
    private final EvidenceRepository          evidenceRepository;
    private final ClerkCaseMapper             caseMapper;
    private final ClerkObjectionMapper        objectionMapper;
    private final AuditService                auditService;
    private final ClerkNotificationHelper     notificationHelper;

    @Override
    @Transactional
    public CaseScrutinyResponse openScrutiny(String caseUuid, String remarks) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = loadAndValidateCourtAccess(caseUuid, clerk.getCourt().getId());

        if (caseFile.getStatus() != CaseStatus.SUBMITTED) {
            throw new BusinessRuleViolationException(
                    "Only SUBMITTED cases can be opened for scrutiny. Current: " + caseFile.getStatus());
        }

        CaseStatus from = caseFile.getStatus();
        caseFile.setStatus(CaseStatus.UNDER_SCRUTINY);
        caseFile.setScrutinyClerkUuid(clerk.getUuid());
        caseFile.setVerificationRemarks(remarks);
        caseFileRepository.save(caseFile);

        recordStatusHistory(caseFile, from, CaseStatus.UNDER_SCRUTINY, clerk.getUuid(), "CLERK", remarks);
        auditService.logSuccess("CASE_SCRUTINY_OPENED", "CaseFile", caseUuid,
                "Clerk " + clerk.getUuid() + " opened scrutiny for case " + caseFile.getCaseNumber());

        return enrichWithCounts(caseMapper.toCaseScrutinyResponse(caseFile), caseFile);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseScrutinyResponse getCaseDetail(String caseUuid) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = loadAndValidateCourtAccess(caseUuid, clerk.getCourt().getId());
        return enrichWithCounts(caseMapper.toCaseScrutinyResponse(caseFile), caseFile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClerkCaseSummaryResponse> getCases(List<CaseStatus> statuses, Pageable pageable) {
        var clerk = securityUtil.getCurrentClerk();
        Long courtId = clerk.getCourt().getId();
        List<CaseStatus> filter = (statuses == null || statuses.isEmpty())
                ? List.copyOf(PENDING_STATUSES)
                : statuses;

        return caseFileRepository
                .findByStatusInAndCourtIdAndIsDeletedFalse(filter, courtId, pageable)
                .map(c -> {
                    ClerkCaseSummaryResponse dto = caseMapper.toClerkCaseSummaryResponse(c);
                    dto.setOpenObjectionCount(objectionRepository
                            .countByCaseFileIdAndIsResolvedFalseAndIsDeletedFalse(c.getId()));
                    return dto;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClerkCaseSummaryResponse> searchCases(String keyword, Pageable pageable) {
        var clerk = securityUtil.getCurrentClerk();
        return caseFileRepository
                .searchByCourtIdAndKeyword(clerk.getCourt().getId(), keyword, pageable)
                .map(c -> {
                    ClerkCaseSummaryResponse dto = caseMapper.toClerkCaseSummaryResponse(c);
                    dto.setOpenObjectionCount(objectionRepository
                            .countByCaseFileIdAndIsResolvedFalseAndIsDeletedFalse(c.getId()));
                    return dto;
                });
    }

    @Override
    @Transactional
    public ObjectionResponse raiseObjection(String caseUuid, RaiseObjectionRequest request) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = loadAndValidateCourtAccess(caseUuid, clerk.getCourt().getId());

        if (caseFile.getStatus() != CaseStatus.UNDER_SCRUTINY) {
            throw new BusinessRuleViolationException(
                    "Objections can only be raised on UNDER_SCRUTINY cases. Current: " + caseFile.getStatus());
        }

        CaseObjection objection = CaseObjection.builder()
                .caseFile(caseFile)
                .raisedByClerkUuid(clerk.getUuid())
                .objectionType(request.getObjectionType())
                .reason(request.getReason())
                .missingDocuments(request.getMissingDocuments())
                .correctionRequired(request.getCorrectionRequired())
                .isResolved(Boolean.FALSE)
                .build();

        CaseObjection saved = objectionRepository.save(objection);

        auditService.logSuccess("OBJECTION_RAISED", "CaseObjection", saved.getUuid(),
                "Clerk " + clerk.getUuid() + " raised " + request.getObjectionType() + " objection on case " + caseFile.getCaseNumber());

        log.info("Objection raised on case {} by clerk {}", caseFile.getCaseNumber(), clerk.getUuid());
        return objectionMapper.toObjectionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ObjectionResponse> getObjections(String caseUuid) {
        var clerk = securityUtil.getCurrentClerk();
        loadAndValidateCourtAccess(caseUuid, clerk.getCourt().getId());
        return objectionRepository.findByCaseFileUuidAndIsDeletedFalseOrderByCreatedAtDesc(caseUuid)
                .stream().map(objectionMapper::toObjectionResponse).toList();
    }

    @Override
    @Transactional
    public CaseScrutinyResponse verifyCase(String caseUuid, ScrutinyActionRequest request) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = loadAndValidateCourtAccess(caseUuid, clerk.getCourt().getId());

        if (caseFile.getStatus() != CaseStatus.UNDER_SCRUTINY) {
            throw new BusinessRuleViolationException(
                    "Only UNDER_SCRUTINY cases can be verified. Current: " + caseFile.getStatus());
        }

        caseFile.setVerificationRemarks(request.getRemarks());
        caseFile.setIsJurisdictionVerified(Boolean.TRUE);
        caseFileRepository.save(caseFile);

        auditService.logSuccess("CASE_VERIFIED", "CaseFile", caseUuid,
                "Clerk " + clerk.getUuid() + " verified case " + caseFile.getCaseNumber());

        return enrichWithCounts(caseMapper.toCaseScrutinyResponse(caseFile), caseFile);
    }

    @Override
    @Transactional
    public CaseScrutinyResponse returnCase(String caseUuid, ScrutinyActionRequest request) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = loadAndValidateCourtAccess(caseUuid, clerk.getCourt().getId());

        if (caseFile.getStatus() != CaseStatus.UNDER_SCRUTINY) {
            throw new BusinessRuleViolationException(
                    "Only UNDER_SCRUTINY cases can be returned. Current: " + caseFile.getStatus());
        }

        CaseStatus from = caseFile.getStatus();
        caseFile.setStatus(CaseStatus.RETURNED);
        caseFile.setVerificationRemarks(request.getRemarks());
        caseFileRepository.save(caseFile);

        recordStatusHistory(caseFile, from, CaseStatus.RETURNED, clerk.getUuid(), "CLERK", request.getRemarks());
        auditService.logSuccess("CASE_RETURNED", "CaseFile", caseUuid,
                "Clerk " + clerk.getUuid() + " returned case " + caseFile.getCaseNumber());

        // Notify advocate
        if (caseFile.getPetitionerAdvocate() != null && caseFile.getPetitionerAdvocate().getUser() != null) {
            notificationHelper.notifyCaseReturned(
                    caseFile.getPetitionerAdvocate().getUser().getUuid(),
                    caseUuid, caseFile.getCaseNumber(), request.getRemarks());
        }

        log.info("Case {} returned by clerk {}", caseFile.getCaseNumber(), clerk.getUuid());
        return enrichWithCounts(caseMapper.toCaseScrutinyResponse(caseFile), caseFile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTimelineResponse> getCaseTimeline(String caseUuid) {
        var clerk = securityUtil.getCurrentClerk();
        loadAndValidateCourtAccess(caseUuid, clerk.getCourt().getId());

        return statusHistoryRepository
                .findByCaseFileUuidOrderByChangedAtDesc(caseUuid)
                .stream()
                .map(h -> CaseTimelineResponse.builder()
                        .uuid(h.getUuid())
                        .fromStatus(h.getFromStatus())
                        .toStatus(h.getToStatus())
                        .changedAt(h.getChangedAt())
                        .changedByUuid(h.getChangedByUuid())
                        .changedByRole(h.getChangedByRole())
                        .remarks(h.getRemarks())
                        .build())
                .toList();
    }

    // ── Private Helpers ───────────────────────────────────────────────────

    private CaseFile loadAndValidateCourtAccess(String caseUuid, Long clerkCourtId) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        if (caseFile.getCourt() == null ||
                !caseFile.getCourt().getId().equals(clerkCourtId)) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your assigned court.");
        }
        return caseFile;
    }

    private void recordStatusHistory(CaseFile caseFile, CaseStatus from, CaseStatus to,
                                     String changedByUuid, String role, String remarks) {
        CaseStatusHistory history = CaseStatusHistory.builder()
                .caseFile(caseFile)
                .fromStatus(from)
                .toStatus(to)
                .changedAt(LocalDateTime.now())
                .changedByUuid(changedByUuid)
                .changedByRole(role)
                .remarks(remarks)
                .build();
        statusHistoryRepository.save(history);
    }

    private CaseScrutinyResponse enrichWithCounts(CaseScrutinyResponse dto, CaseFile caseFile) {
        dto.setDocumentCount(documentRepository.countByCaseFileUuidAndIsDeletedFalse(caseFile.getUuid()));
        dto.setUnverifiedDocumentCount(
                documentRepository.findByCaseFileUuidAndIsDeletedFalse(caseFile.getUuid())
                        .stream().filter(d -> !Boolean.TRUE.equals(d.getIsVerified())).count());
        dto.setEvidenceCount(evidenceRepository.findByCaseFileUuidAndIsDeletedFalse(caseFile.getUuid()).size());
        dto.setUnverifiedEvidenceCount(
                evidenceRepository.findByCaseFileUuidAndIsDeletedFalse(caseFile.getUuid())
                        .stream().filter(e -> !Boolean.TRUE.equals(e.getIsVerified())).count());
        dto.setOpenObjectionCount(
                objectionRepository.countByCaseFileIdAndIsResolvedFalseAndIsDeletedFalse(caseFile.getId()));
        return dto;
    }
}
