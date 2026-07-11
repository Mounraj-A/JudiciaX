package com.courtai.clerk.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.clerk.dto.EvidenceVerificationResponse;
import com.courtai.clerk.dto.VerifyEvidenceRequest;
import com.courtai.clerk.service.ClerkEvidenceVerificationService;
import com.courtai.clerk.util.ClerkNotificationHelper;
import com.courtai.clerk.util.ClerkSecurityUtil;
import com.courtai.evidence.entity.Evidence;
import com.courtai.evidence.repository.EvidenceRepository;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Implementation of {@link ClerkEvidenceVerificationService}. */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClerkEvidenceVerificationServiceImpl implements ClerkEvidenceVerificationService {

    private final ClerkSecurityUtil       securityUtil;
    private final CaseFileRepository      caseFileRepository;
    private final EvidenceRepository      evidenceRepository;
    private final AuditService            auditService;
    private final ClerkNotificationHelper notificationHelper;

    @Override
    @Transactional(readOnly = true)
    public Page<EvidenceVerificationResponse> getEvidence(String caseUuid, Pageable pageable) {
        var clerk = securityUtil.getCurrentClerk();
        assertCourtOwnership(caseUuid, clerk.getCourt().getId());

        List<Evidence> evidenceList = evidenceRepository.findByCaseFileUuidAndIsDeletedFalse(caseUuid);
        List<EvidenceVerificationResponse> responses = evidenceList.stream()
                .map(this::toResponse).toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public EvidenceVerificationResponse getEvidenceByUuid(String caseUuid, String evidenceUuid) {
        var clerk = securityUtil.getCurrentClerk();
        assertCourtOwnership(caseUuid, clerk.getCourt().getId());

        Evidence evidence = evidenceRepository.findByUuidAndIsDeletedFalse(evidenceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence", "uuid", evidenceUuid));
        return toResponse(evidence);
    }

    @Override
    @Transactional
    public EvidenceVerificationResponse verifyEvidence(String caseUuid, String evidenceUuid,
                                                        VerifyEvidenceRequest request) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = assertCourtOwnership(caseUuid, clerk.getCourt().getId());

        if (!request.isApproved() && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new BusinessRuleViolationException("Rejection reason is required when rejecting evidence.");
        }

        Evidence evidence = evidenceRepository.findByUuidAndIsDeletedFalse(evidenceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence", "uuid", evidenceUuid));

        evidence.setIsVerified(request.isApproved());
        evidence.setVerificationRemarks(request.getRemarks());
        evidence.setVerifiedByUuid(clerk.getUuid());
        evidence.setVerifiedAt(LocalDateTime.now());
        if (!request.isApproved()) {
            evidence.setRejectionReason(request.getRejectionReason());
        }
        evidenceRepository.save(evidence);

        String action = request.isApproved() ? "EVIDENCE_VERIFIED" : "EVIDENCE_REJECTED";
        auditService.logSuccess(action, "Evidence", evidenceUuid,
                "Clerk " + clerk.getUuid() + (request.isApproved() ? " verified" : " rejected")
                + " evidence '" + evidence.getTitle() + "' for case " + caseFile.getCaseNumber());

        if (!request.isApproved() && caseFile.getPetitionerAdvocate() != null
                && caseFile.getPetitionerAdvocate().getUser() != null) {
            notificationHelper.notifyEvidenceRejected(
                    caseFile.getPetitionerAdvocate().getUser().getUuid(),
                    caseUuid, caseFile.getCaseNumber(),
                    evidence.getTitle(), request.getRejectionReason());
        }

        log.info("Evidence {} {} by clerk {}", evidenceUuid,
                request.isApproved() ? "verified" : "rejected", clerk.getUuid());
        return toResponse(evidence);
    }

    // ── Private ───────────────────────────────────────────────────────────

    private CaseFile assertCourtOwnership(String caseUuid, Long clerkCourtId) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        if (caseFile.getCourt() == null || !caseFile.getCourt().getId().equals(clerkCourtId)) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your assigned court.");
        }
        return caseFile;
    }

    private EvidenceVerificationResponse toResponse(Evidence e) {
        return EvidenceVerificationResponse.builder()
                .uuid(e.getUuid())
                .caseUuid(e.getCaseFile().getUuid())
                .caseNumber(e.getCaseFile().getCaseNumber())
                .evidenceType(e.getEvidenceType())
                .title(e.getTitle())
                .description(e.getDescription())
                .collectedBy(e.getCollectedBy())
                .collectedAt(e.getCollectedAt())
                .location(e.getLocation())
                .isAdmitted(e.getIsAdmitted())
                .isVerified(e.getIsVerified())
                .verifiedByUuid(e.getVerifiedByUuid())
                .verifiedAt(e.getVerifiedAt())
                .verificationRemarks(e.getVerificationRemarks())
                .rejectionReason(e.getRejectionReason())
                .linkedDocumentUuid(e.getDocument() != null ? e.getDocument().getUuid() : null)
                .createdAt(e.getCreatedAt())
                .build();
    }
}
