package com.courtai.casemanagement.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseFlag;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casefile.repository.CaseFlagRepository;
import com.courtai.casemanagement.dto.*;
import com.courtai.casemanagement.mapper.CaseMapper;
import com.courtai.casemanagement.service.CaseManagementService;
import com.courtai.casemanagement.service.CaseTimelineService;
import com.courtai.casemanagement.service.CaseWorkflowService;
import com.courtai.casecategory.repository.CaseCategoryRepository;
import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.CaseType;
import com.courtai.court.repository.CourtRepository;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseManagementServiceImpl implements CaseManagementService {

    private static final String ENTITY_TYPE = "CaseFile";

    private final CaseFileRepository     caseFileRepository;
    private final CaseFlagRepository     caseFlagRepository;
    private final CourtRepository        courtRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final CaseWorkflowService    workflowService;
    private final CaseTimelineService    timelineService;
    private final CaseMapper             caseMapper;
    private final AuditService           auditService;
    private final NotificationService    notificationService;

    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse createCase(CaseCreateRequest req, String actorUuid, String actorRole) {
        CaseFile caseFile = CaseFile.builder()
                .caseNumber(generateCaseNumber())
                .caseTitle(req.getCaseTitle())
                .caseDescription(req.getCaseDescription())
                .caseType(req.getCaseType() != null ? req.getCaseType() : CaseType.OTHER)
                .petitionerName(req.getPetitionerName())
                .respondentName(req.getRespondentName())
                .filingDate(req.getFilingDate() != null ? req.getFilingDate() : LocalDate.now())
                .filingYear(LocalDate.now().getYear())
                .policeStation(req.getPoliceStation())
                .actSection(req.getActSection())
                .priority(req.getPriority() != null ? req.getPriority() : CasePriority.LOW)
                .status(CaseStatus.DRAFT)
                .build();

        if (req.getCourtUuid() != null) {
            courtRepository.findByUuidAndIsDeletedFalse(req.getCourtUuid())
                    .ifPresent(caseFile::setCourt);
        }
        if (req.getCaseCategoryUuid() != null) {
            caseCategoryRepository.findByUuidAndIsDeletedFalse(req.getCaseCategoryUuid())
                    .ifPresent(caseFile::setCaseCategory);
        }

        caseFileRepository.save(caseFile);

        // Initialise flags
        CaseFlag flags = CaseFlag.builder()
                .caseFile(caseFile)
                .medicalEmergency(bool(req.getMedicalEmergency()))
                .childInvolved(bool(req.getChildInvolved()))
                .womenSafety(bool(req.getWomenSafety()))
                .seniorCitizen(bool(req.getSeniorCitizen()))
                .disability(bool(req.getDisability()))
                .financialFraud(bool(req.getFinancialFraud()))
                .cyberCrime(bool(req.getCyberCrime()))
                .threatToLife(bool(req.getThreatToLife()))
                .highPublicInterest(bool(req.getHighPublicInterest()))
                .build();
        caseFlagRepository.save(flags);

        // Timeline event
        timelineService.recordEvent(caseFile, "CASE_CREATED", "Case Created",
                actorUuid, actorRole, null, null);

        // Audit
        auditService.logSuccess("CASE_CREATED", ENTITY_TYPE, caseFile.getUuid(),
                "Case created: " + caseFile.getCaseNumber() + " by " + actorUuid);

        log.info("[CASE] Case {} created in DRAFT by {}", caseFile.getCaseNumber(), actorUuid);
        return caseMapper.toDetails(caseFile, flags);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse updateCase(String caseUuid, CaseUpdateRequest req,
                                          String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);

        // Advocates may only edit DRAFT cases
        if ("ROLE_ADVOCATE".equalsIgnoreCase(actorRole)
                && caseFile.getStatus() != CaseStatus.DRAFT) {
            throw new BusinessRuleViolationException(
                    "Advocates may only edit cases in DRAFT status. Current: " + caseFile.getStatus());
        }

        if (req.getCaseTitle()       != null) caseFile.setCaseTitle(req.getCaseTitle());
        if (req.getCaseDescription() != null) caseFile.setCaseDescription(req.getCaseDescription());
        if (req.getPetitionerName()  != null) caseFile.setPetitionerName(req.getPetitionerName());
        if (req.getRespondentName()  != null) caseFile.setRespondentName(req.getRespondentName());
        if (req.getFilingDate()      != null) caseFile.setFilingDate(req.getFilingDate());
        if (req.getPoliceStation()   != null) caseFile.setPoliceStation(req.getPoliceStation());
        if (req.getActSection()      != null) caseFile.setActSection(req.getActSection());
        if (req.getPriority()        != null) caseFile.setPriority(req.getPriority());
        caseFileRepository.save(caseFile);

        // Update flags if any flag is provided
        CaseFlag flags = caseFlagRepository.findByCaseFileIdAndIsDeletedFalse(caseFile.getId())
                .orElse(null);
        if (flags != null) {
            if (req.getMedicalEmergency()  != null) flags.setMedicalEmergency(req.getMedicalEmergency());
            if (req.getChildInvolved()     != null) flags.setChildInvolved(req.getChildInvolved());
            if (req.getWomenSafety()       != null) flags.setWomenSafety(req.getWomenSafety());
            if (req.getSeniorCitizen()     != null) flags.setSeniorCitizen(req.getSeniorCitizen());
            if (req.getDisability()        != null) flags.setDisability(req.getDisability());
            if (req.getFinancialFraud()    != null) flags.setFinancialFraud(req.getFinancialFraud());
            if (req.getCyberCrime()        != null) flags.setCyberCrime(req.getCyberCrime());
            if (req.getThreatToLife()      != null) flags.setThreatToLife(req.getThreatToLife());
            if (req.getHighPublicInterest()!= null) flags.setHighPublicInterest(req.getHighPublicInterest());
            caseFlagRepository.save(flags);
        }

        timelineService.recordEvent(caseFile, "CASE_UPDATED", "Case Updated",
                actorUuid, actorRole, null, null);
        auditService.logSuccess("CASE_UPDATED", ENTITY_TYPE, caseUuid,
                "Updated by " + actorUuid);
        return caseMapper.toDetails(caseFile, flags);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public CaseDetailsResponse getCase(String caseUuid) {
        CaseFile caseFile = loadCase(caseUuid);
        CaseFlag flags    = caseFlagRepository.findByCaseFileIdAndIsDeletedFalse(caseFile.getId())
                .orElse(null);
        return caseMapper.toDetails(caseFile, flags);
    }

    @Override
    public Page<CaseSummaryResponse> listCases(Pageable pageable) {
        return caseFileRepository.findAll(pageable).map(caseMapper::toSummary);
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse submitCase(String caseUuid, String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        workflowService.executeTransition(caseUuid, CaseStatus.SUBMITTED, actorUuid, actorRole,
                "Submitted for court scrutiny");
        timelineService.recordEvent(caseFile, "SUBMITTED", "Case Submitted",
                actorUuid, actorRole, null, null);
        notificationService.sendInAppNotification(actorUuid,
                "Case Submitted", "Your case " + caseFile.getCaseNumber() + " has been submitted.",
                caseFile.getUuid(), ENTITY_TYPE);
        CaseFile updated = loadCase(caseUuid);
        CaseFlag flags = caseFlagRepository.findByCaseFileIdAndIsDeletedFalse(updated.getId()).orElse(null);
        return caseMapper.toDetails(updated, flags);
    }

    // ── Archive ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse archiveCase(String caseUuid, CaseArchiveRequest req,
                                           String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        if (caseFile.getStatus() != CaseStatus.DISPOSED && caseFile.getStatus() != CaseStatus.CLOSED) {
            throw new BusinessRuleViolationException(
                    "Only DISPOSED or CLOSED cases can be archived. Current: " + caseFile.getStatus());
        }
        workflowService.executeTransition(caseUuid, CaseStatus.ARCHIVED, actorUuid, actorRole,
                req.getReason());
        timelineService.recordEvent(loadCase(caseUuid), "ARCHIVED", "Case Archived",
                actorUuid, actorRole, null, req.getReason());
        return getCase(caseUuid);
    }

    // ── Close ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse closeCase(String caseUuid, String actorUuid, String actorRole, String reason) {
        workflowService.executeTransition(caseUuid, CaseStatus.CLOSED, actorUuid, actorRole, reason);
        timelineService.recordEvent(loadCase(caseUuid), "CLOSED", "Case Closed",
                actorUuid, actorRole, null, reason);
        return getCase(caseUuid);
    }

    // ── Reopen ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse reopenCase(String caseUuid, String actorUuid, String actorRole, String reason) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(actorRole)) {
            throw new UnauthorizedActionException("Only admins may reopen a closed case.");
        }
        CaseFile caseFile = loadCase(caseUuid);
        if (caseFile.getStatus() == CaseStatus.ARCHIVED || caseFile.getStatus() == CaseStatus.CANCELLED) {
            throw new BusinessRuleViolationException(
                    "Archived or cancelled cases cannot be reopened.");
        }
        caseFile.setStatus(CaseStatus.REGISTERED);
        caseFileRepository.save(caseFile);
        timelineService.recordEvent(caseFile, "REOPENED", "Case Reopened",
                actorUuid, actorRole, null, reason);
        auditService.logSuccess("CASE_REOPENED", ENTITY_TYPE, caseUuid,
                "Reopened by admin " + actorUuid + ". Reason: " + reason);
        return getCase(caseUuid);
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse cancelCase(String caseUuid, CaseCancelRequest req,
                                          String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        workflowService.executeTransition(caseUuid, CaseStatus.CANCELLED, actorUuid, actorRole,
                req.getReason());
        timelineService.recordEvent(loadCase(caseUuid), "CANCELLED", "Case Cancelled",
                actorUuid, actorRole, null, req.getReason());
        return getCase(caseUuid);
    }

    // ── Clone ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseCloneResponse cloneCase(String caseUuid, CaseCloneRequest req,
                                       String actorUuid, String actorRole) {
        CaseFile original = loadCase(caseUuid);
        if (original.getStatus() != CaseStatus.DRAFT && original.getStatus() != CaseStatus.SUBMITTED) {
            throw new BusinessRuleViolationException(
                    "Only DRAFT or SUBMITTED cases can be cloned. Current: " + original.getStatus());
        }

        CaseFile clone = CaseFile.builder()
                .caseNumber(generateCaseNumber())
                .caseTitle(req.getNewCaseTitle())
                .caseDescription(original.getCaseDescription())
                .caseType(original.getCaseType())
                .petitionerName(original.getPetitionerName())
                .respondentName(original.getRespondentName())
                .filingDate(LocalDate.now())
                .filingYear(LocalDate.now().getYear())
                .policeStation(original.getPoliceStation())
                .actSection(original.getActSection())
                .priority(original.getPriority())
                .status(CaseStatus.DRAFT)
                .court(original.getCourt())
                .caseCategory(original.getCaseCategory())
                .petitionerAdvocate(original.getPetitionerAdvocate())
                .respondentAdvocate(original.getRespondentAdvocate())
                .build();
        caseFileRepository.save(clone);

        // Copy flags
        caseFlagRepository.findByCaseFileIdAndIsDeletedFalse(original.getId())
                .ifPresent(origFlags -> {
                    CaseFlag clonedFlags = CaseFlag.builder()
                            .caseFile(clone)
                            .medicalEmergency(origFlags.getMedicalEmergency())
                            .childInvolved(origFlags.getChildInvolved())
                            .womenSafety(origFlags.getWomenSafety())
                            .seniorCitizen(origFlags.getSeniorCitizen())
                            .disability(origFlags.getDisability())
                            .financialFraud(origFlags.getFinancialFraud())
                            .cyberCrime(origFlags.getCyberCrime())
                            .threatToLife(origFlags.getThreatToLife())
                            .highPublicInterest(origFlags.getHighPublicInterest())
                            .build();
                    caseFlagRepository.save(clonedFlags);
                });

        timelineService.recordEvent(clone, "CASE_CLONED",
                "Case Cloned from " + original.getCaseNumber(),
                actorUuid, actorRole, null, req.getNotes());
        timelineService.recordEvent(original, "CLONE_CREATED",
                "Clone created: " + clone.getCaseNumber(),
                actorUuid, actorRole, null, null);
        auditService.logSuccess("CASE_CLONED", ENTITY_TYPE, clone.getUuid(),
                "Cloned from " + original.getUuid() + " by " + actorUuid);

        return CaseCloneResponse.builder()
                .originalCaseUuid(original.getUuid())
                .originalCaseNumber(original.getCaseNumber())
                .clonedCaseUuid(clone.getUuid())
                .clonedCaseNumber(clone.getCaseNumber())
                .message("Case cloned successfully in DRAFT status.")
                .build();
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteCase(String caseUuid, String actorUuid, String actorRole) {
        CaseFile caseFile = loadCase(caseUuid);
        if (caseFile.getStatus() != CaseStatus.DRAFT) {
            throw new BusinessRuleViolationException(
                    "Only DRAFT cases can be deleted. Current: " + caseFile.getStatus());
        }
        caseFile.softDelete();
        caseFileRepository.save(caseFile);
        auditService.logSuccess("CASE_DELETED", ENTITY_TYPE, caseUuid,
                "Soft-deleted by " + actorUuid);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CaseFile loadCase(String caseUuid) {
        return caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
    }

    private String generateCaseNumber() {
        return "CASE-" + LocalDate.now().getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Boolean bool(Boolean value) {
        return value != null ? value : Boolean.FALSE;
    }
}
