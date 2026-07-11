package com.courtai.advocate.service.impl;

import com.courtai.advocate.dto.DocumentResponse;
import com.courtai.advocate.mapper.DocumentMapper;
import com.courtai.advocate.service.AdvocateDocumentService;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.DocumentType;
import com.courtai.common.enums.OcrStatus;
import com.courtai.document.entity.Document;
import com.courtai.document.repository.DocumentRepository;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of {@link AdvocateDocumentService}.
 *
 * <p>Files are stored locally at {@code app.upload.storage-path}.
 * Replace this service's storage logic with an S3 client in Phase 2.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvocateDocumentServiceImpl implements AdvocateDocumentService {

    private final AdvocateSecurityUtil  securityUtil;
    private final CaseFileRepository    caseFileRepository;
    private final DocumentRepository    documentRepository;
    private final DocumentMapper        documentMapper;
    private final AuditService          auditService;

    @Value("${app.upload.storage-path:uploads}")
    private String storagePath;

    @Value("${app.upload.max-file-size-mb:50}")
    private long maxFileSizeMb;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/png",
            "image/jpeg",
            "video/mp4",
            "audio/mpeg",
            "audio/wav"
    );

    @Override
    @Transactional
    public DocumentResponse uploadDocument(String caseUuid, MultipartFile file,
                                           String documentType, String description) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BusinessRuleViolationException(
                    "File type '" + contentType + "' is not allowed. Permitted: PDF, DOCX, PNG, JPG, MP4, MP3, WAV");
        }

        // Validate file size
        long maxBytes = maxFileSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BusinessRuleViolationException(
                    "File size exceeds maximum allowed size of " + maxFileSizeMb + " MB.");
        }

        // Save file locally
        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetDir  = Paths.get(storagePath, "documents", caseUuid);
        Path targetPath = targetDir.resolve(storedName);

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to save document file: {}", e.getMessage());
            throw new BusinessRuleViolationException("Failed to save file: " + e.getMessage());
        }

        // Resolve DocumentType enum
        DocumentType docType;
        try {
            docType = documentType != null ? DocumentType.valueOf(documentType.toUpperCase()) : DocumentType.OTHER;
        } catch (IllegalArgumentException e) {
            docType = DocumentType.OTHER;
        }

        Document document = Document.builder()
                .caseFile(caseFile)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedName)
                .fileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : storedName)
                .documentType(docType)
                .mimeType(contentType)
                .fileSizeBytes(file.getSize())
                .storagePath(targetPath.toString())
                .description(description)
                .uploadedByUuid(advocate.getUuid())
                .isVerified(false)
                .isConfidential(false)
                .ocrStatus(OcrStatus.PENDING)
                .version(1)
                .build();

        Document saved = documentRepository.save(document);

        auditService.logSuccess(
                "DOCUMENT_UPLOADED",
                "Document",
                saved.getUuid(),
                "Document '" + file.getOriginalFilename() + "' uploaded for case " + caseUuid);

        log.info("Document uploaded: {} for case: {}", saved.getUuid(), caseUuid);
        return documentMapper.toDocumentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getDocuments(String caseUuid, Pageable pageable) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        List<Document> documents = documentRepository.findByCaseFileUuidAndIsDeletedFalse(caseUuid);
        List<DocumentResponse> responses = documents.stream()
                .map(documentMapper::toDocumentResponse)
                .toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocument(String caseUuid, String documentUuid) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        Document document = documentRepository.findByUuidAndIsDeletedFalse(documentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "uuid", documentUuid));
        return documentMapper.toDocumentResponse(document);
    }

    @Override
    @Transactional
    public void deleteDocument(String caseUuid, String documentUuid) {
        var advocate = securityUtil.getCurrentAdvocate();
        assertCaseOwnership(caseUuid, advocate.getUuid());

        Document document = documentRepository.findByUuidAndIsDeletedFalse(documentUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "uuid", documentUuid));

        // Guard: cannot delete verified documents
        if (Boolean.TRUE.equals(document.getIsVerified())) {
            throw new BusinessRuleViolationException(
                    "Cannot delete a verified document. Contact the court clerk.");
        }

        document.softDelete();
        documentRepository.save(document);

        auditService.logSuccess(
                "DOCUMENT_DELETED",
                "Document",
                documentUuid,
                "Document deleted by advocate " + advocate.getUuid() + " from case " + caseUuid);

        log.info("Document soft-deleted: {} from case: {}", documentUuid, caseUuid);
    }

    // ── Private Helpers ───────────────────────────────────────────────────

    private void assertCaseOwnership(String caseUuid, String advocateUuid) {
        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocateUuid)) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your account.");
        }
    }
}
