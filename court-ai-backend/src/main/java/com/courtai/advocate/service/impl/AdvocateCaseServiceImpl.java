package com.courtai.advocate.service.impl;

import com.courtai.advocate.dto.CaseResponse;
import com.courtai.advocate.dto.CaseSummaryResponse;
import com.courtai.advocate.dto.CreateCaseRequest;
import com.courtai.advocate.dto.UpdateCaseRequest;
import com.courtai.advocate.entity.Advocate;
import com.courtai.advocate.mapper.CaseMapper;
import com.courtai.advocate.service.AdvocateCaseService;
import com.courtai.advocate.util.AdvocateSecurityUtil;
import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casecategory.repository.CaseCategoryRepository;
import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import com.courtai.court.repository.CourtRepository;
import com.courtai.exception.DuplicateResourceException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

/**
 * Implementation of {@link AdvocateCaseService}.
 *
 * <p>Case Number format: {@code ADV-{YEAR}-{6-digit-random}} (e.g., ADV-2025-482931)</p>
 * <p>Every mutating operation validates advocate ownership before proceeding.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvocateCaseServiceImpl implements AdvocateCaseService {

    private final AdvocateSecurityUtil  securityUtil;
    private final CaseFileRepository    caseFileRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final CourtRepository       courtRepository;
    private final CaseMapper            caseMapper;
    private final AuditService          auditService;

    // ── Case Number Generation ────────────────────────────────────────────

    private String generateCaseNumber() {
        int year    = Year.now().getValue();
        String rand = String.valueOf(100000 + (int)(Math.random() * 900000));
        String candidate = "ADV-" + year + "-" + rand;
        // Retry if collision (extremely rare)
        while (caseFileRepository.existsByCaseNumber(candidate)) {
            rand = String.valueOf(100000 + (int)(Math.random() * 900000));
            candidate = "ADV-" + year + "-" + rand;
        }
        return candidate;
    }

    @Override
    @Transactional
    public CaseResponse createCase(CreateCaseRequest request) {
        Advocate advocate = securityUtil.getCurrentAdvocate();

        // Guard: advocate must have a bar council number registered before filing
        if (advocate.getBarCouncilNumber() == null || advocate.getBarCouncilNumber().isBlank()) {
            throw new UnauthorizedActionException(
                    "Please complete your advocate profile with a valid Bar Council Registration Number before filing cases.");
        }

        String caseNumber = generateCaseNumber();

        CaseFile.CaseFileBuilder builder = CaseFile.builder()
                .caseNumber(caseNumber)
                .caseTitle(request.getCaseTitle())
                .caseDescription(request.getCaseDescription())
                .caseType(request.getCaseType() != null ? request.getCaseType() : CaseType.CIVIL)
                .status(CaseStatus.FILED)
                .priority(request.getPriority() != null ? request.getPriority() : CasePriority.LOW)
                .petitionerName(request.getPetitionerName())
                .respondentName(request.getRespondentName())
                .filingDate(request.getFilingDate() != null ? request.getFilingDate() : LocalDate.now())
                .filingYear(Year.now().getValue())
                .policeStation(request.getPoliceStation())
                .actSection(request.getActSection())
                .petitionerAdvocate(advocate);

        // Resolve court
        if (request.getCourtUuid() != null) {
            courtRepository.findByUuidAndIsDeletedFalse(request.getCourtUuid())
                    .ifPresent(court -> {
                        builder.court(court);
                        builder.courtName(court.getCourtName());
                    });
        }

        // Resolve case category
        if (request.getCaseCategoryUuid() != null) {
            caseCategoryRepository.findByUuidAndIsDeletedFalse(request.getCaseCategoryUuid())
                    .ifPresent(builder::caseCategory);
        }

        CaseFile saved = caseFileRepository.save(builder.build());

        auditService.logSuccess(
                "CASE_CREATED",
                "CaseFile",
                saved.getUuid(),
                "Case filed by advocate " + advocate.getUuid() + " | Case: " + caseNumber);

        log.info("Case created: {} by advocate: {}", caseNumber, advocate.getUuid());
        return caseMapper.toCaseResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseSummaryResponse> getMyCases(Pageable pageable) {
        Advocate advocate = securityUtil.getCurrentAdvocate();
        return caseFileRepository
                .findByAdvocateUuid(advocate.getUuid(), pageable)
                .map(caseMapper::toCaseSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseResponse getCaseByUuid(String caseUuid) {
        Advocate advocate = securityUtil.getCurrentAdvocate();
        assertOwnership(caseUuid, advocate.getUuid());
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        return caseMapper.toCaseResponse(caseFile);
    }

    @Override
    @Transactional
    public CaseResponse updateCase(String caseUuid, UpdateCaseRequest request) {
        Advocate advocate = securityUtil.getCurrentAdvocate();
        assertOwnership(caseUuid, advocate.getUuid());

        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        // Guard: only allow updates in mutable states
        if (caseFile.getStatus() == CaseStatus.DISPOSED ||
                caseFile.getStatus() == CaseStatus.CLOSED) {
            throw new UnauthorizedActionException(
                    "Case cannot be updated in status: " + caseFile.getStatus());
        }

        if (request.getCaseTitle()       != null) caseFile.setCaseTitle(request.getCaseTitle());
        if (request.getCaseDescription() != null) caseFile.setCaseDescription(request.getCaseDescription());
        if (request.getPoliceStation()   != null) caseFile.setPoliceStation(request.getPoliceStation());
        if (request.getActSection()      != null) caseFile.setActSection(request.getActSection());
        if (request.getPetitionerName()  != null) caseFile.setPetitionerName(request.getPetitionerName());
        if (request.getRespondentName()  != null) caseFile.setRespondentName(request.getRespondentName());

        CaseFile saved = caseFileRepository.save(caseFile);

        auditService.logSuccess(
                "CASE_UPDATED",
                "CaseFile",
                saved.getUuid(),
                "Case updated by advocate " + advocate.getUuid());

        return caseMapper.toCaseResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseSummaryResponse> getCasesByStatus(CaseStatus status, Pageable pageable) {
        Advocate advocate = securityUtil.getCurrentAdvocate();
        return caseFileRepository
                .findByAdvocateUuidAndStatus(advocate.getUuid(), status, pageable)
                .map(caseMapper::toCaseSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseSummaryResponse> searchCases(String keyword, Pageable pageable) {
        Advocate advocate = securityUtil.getCurrentAdvocate();
        return caseFileRepository
                .searchByAdvocateUuidAndKeyword(advocate.getUuid(), keyword, pageable)
                .map(caseMapper::toCaseSummaryResponse);
    }

    // ── Private Helpers ───────────────────────────────────────────────────

    private void assertOwnership(String caseUuid, String advocateUuid) {
        if (!caseFileRepository.existsByUuidAndAdvocateUuid(caseUuid, advocateUuid)) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your account.");
        }
    }
}
