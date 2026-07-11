package com.courtai.clerk.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.clerk.dto.DocumentVerificationResponse;
import com.courtai.clerk.dto.VerifyDocumentRequest;
import com.courtai.clerk.mapper.ClerkDocumentMapper;
import com.courtai.clerk.service.ClerkDocumentVerificationService;
import com.courtai.clerk.util.ClerkNotificationHelper;
import com.courtai.clerk.util.ClerkSecurityUtil;
import com.courtai.document.entity.Document;
import com.courtai.document.repository.DocumentRepository;
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

/** Implementation of {@link ClerkDocumentVerificationService}. */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClerkDocumentVerificationServiceImpl implements ClerkDocumentVerificationService {

    private final ClerkSecurityUtil      securityUtil;
    private final CaseFileRepository     caseFileRepository;
    private final DocumentRepository     documentRepository;
    private final ClerkDocumentMapper    documentMapper;
    private final AuditService           auditService;
    private final ClerkNotificationHelper notificationHelper;

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentVerificationResponse> getDocuments(String caseUuid, Pageable pageable) {
        var clerk = securityUtil.getCurrentClerk();
        assertCourtOwnership(caseUuid, clerk.getCourt().getId());

        List<Document> docs = documentRepository.findByCaseFileUuidAndIsDeletedFalse(caseUuid);
        List<DocumentVerificationResponse> responses = docs.stream()
                .map(documentMapper::toDocumentVerificationResponse).toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentVerificationResponse getDocument(String caseUuid, String documentUuid) {
        var clerk = securityUtil.getCurrentClerk();
        assertCourtOwnership(caseUuid, clerk.getCourt().getId());

        Document doc = documentRepository.findByUuidAndIsDeletedFalse(documentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "uuid", documentUuid));
        return documentMapper.toDocumentVerificationResponse(doc);
    }

    @Override
    @Transactional
    public DocumentVerificationResponse verifyDocument(String caseUuid, String documentUuid,
                                                       VerifyDocumentRequest request) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = assertCourtOwnership(caseUuid, clerk.getCourt().getId());

        if (!request.isApproved() && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new BusinessRuleViolationException("Rejection reason is required when rejecting a document.");
        }

        Document doc = documentRepository.findByUuidAndIsDeletedFalse(documentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "uuid", documentUuid));

        doc.setIsVerified(request.isApproved());
        doc.setVerificationRemarks(request.getRemarks());
        doc.setVerifiedByUuid(clerk.getUuid());
        doc.setVerifiedAt(LocalDateTime.now());
        if (!request.isApproved()) {
            doc.setRejectionReason(request.getRejectionReason());
        }
        documentRepository.save(doc);

        String action = request.isApproved() ? "DOCUMENT_VERIFIED" : "DOCUMENT_REJECTED";
        auditService.logSuccess(action, "Document", documentUuid,
                "Clerk " + clerk.getUuid() + (request.isApproved() ? " verified" : " rejected")
                + " document " + doc.getOriginalFileName() + " for case " + caseFile.getCaseNumber());

        // Notify advocate on rejection
        if (!request.isApproved() && caseFile.getPetitionerAdvocate() != null
                && caseFile.getPetitionerAdvocate().getUser() != null) {
            notificationHelper.notifyDocumentRejected(
                    caseFile.getPetitionerAdvocate().getUser().getUuid(),
                    caseUuid, caseFile.getCaseNumber(),
                    doc.getOriginalFileName(), request.getRejectionReason());
        }

        log.info("Document {} {} by clerk {}", documentUuid,
                request.isApproved() ? "verified" : "rejected", clerk.getUuid());
        return documentMapper.toDocumentVerificationResponse(doc);
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
}
