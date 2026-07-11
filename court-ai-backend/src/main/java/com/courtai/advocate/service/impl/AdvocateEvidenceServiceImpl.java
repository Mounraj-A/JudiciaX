package com.courtai.advocate.service.impl;

import com.courtai.advocate.dto.CreateEvidenceRequest;
import com.courtai.advocate.dto.EvidenceResponse;
import com.courtai.advocate.service.AdvocateEvidenceService;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.document.entity.Document;
import com.courtai.document.repository.DocumentRepository;
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

import java.util.List;

/**
 * Implementation of {@link AdvocateEvidenceService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvocateEvidenceServiceImpl implements AdvocateEvidenceService {

    private final AdvocateSecurityUtil  securityUtil;
    private final CaseFileRepository    caseFileRepository;
    private final EvidenceRepository    evidenceRepository;
    private final DocumentRepository    documentRepository;
    private final AuditService          auditService;

    @Override
    @Transactional
    public EvidenceResponse submitEvidence(String caseUuid, CreateEvidenceRequest request) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        Evidence.EvidenceBuilder builder = Evidence.builder()
                .caseFile(caseFile)
                .evidenceType(request.getEvidenceType())
                .title(request.getTitle())
                .description(request.getDescription())
                .collectedBy(request.getCollectedBy())
                .collectedAt(request.getCollectedAt())
                .location(request.getLocation())
                .isAdmitted(false);

        // Link document if provided
        if (request.getDocumentUuid() != null) {
            Document document = documentRepository.findByUuidAndIsDeletedFalse(request.getDocumentUuid())
                    .orElseThrow(() -> new ResourceNotFoundException("Document", "uuid", request.getDocumentUuid()));
            builder.document(document);
        }

        Evidence saved = evidenceRepository.save(builder.build());

        auditService.logSuccess(
                "EVIDENCE_UPLOADED",
                "Evidence",
                saved.getUuid(),
                "Evidence '" + request.getTitle() + "' submitted for case " + caseUuid);

        log.info("Evidence submitted: {} for case: {}", saved.getUuid(), caseUuid);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvidenceResponse> getEvidence(String caseUuid, Pageable pageable) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        List<Evidence> evidenceList = evidenceRepository.findByCaseFileUuidAndIsDeletedFalse(caseUuid);
        List<EvidenceResponse> responses = evidenceList.stream().map(this::toResponse).toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public EvidenceResponse getEvidenceByUuid(String caseUuid, String evidenceUuid) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        Evidence evidence = evidenceRepository.findByUuidAndIsDeletedFalse(evidenceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence", "uuid", evidenceUuid));
        return toResponse(evidence);
    }

    @Override
    @Transactional
    public void deleteEvidence(String caseUuid, String evidenceUuid) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        Evidence evidence = evidenceRepository.findByUuidAndIsDeletedFalse(evidenceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence", "uuid", evidenceUuid));

        if (Boolean.TRUE.equals(evidence.getIsAdmitted())) {
            throw new BusinessRuleViolationException(
                    "Cannot delete court-admitted evidence. Contact the judge or clerk.");
        }

        evidence.softDelete();
        evidenceRepository.save(evidence);

        auditService.logSuccess(
                "EVIDENCE_DELETED",
                "Evidence",
                evidenceUuid,
                "Evidence deleted by advocate " + advocate.getUuid());

        log.info("Evidence soft-deleted: {} from case: {}", evidenceUuid, caseUuid);
    }

    // ── Private Mapper ────────────────────────────────────────────────────

    private EvidenceResponse toResponse(Evidence evidence) {
        return EvidenceResponse.builder()
                .uuid(evidence.getUuid())
                .evidenceType(evidence.getEvidenceType())
                .title(evidence.getTitle())
                .description(evidence.getDescription())
                .collectedBy(evidence.getCollectedBy())
                .collectedAt(evidence.getCollectedAt())
                .location(evidence.getLocation())
                .isAdmitted(evidence.getIsAdmitted())
                .admissionRemarks(evidence.getAdmissionRemarks())
                .documentUuid(evidence.getDocument() != null ? evidence.getDocument().getUuid() : null)
                .documentFileName(evidence.getDocument() != null ? evidence.getDocument().getOriginalFileName() : null)
                .createdAt(evidence.getCreatedAt())
                .updatedAt(evidence.getUpdatedAt())
                .build();
    }

    private void assertCaseOwnership(String caseUuid, String advocateUuid) {
        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocateUuid)) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your account.");
        }
    }
}
