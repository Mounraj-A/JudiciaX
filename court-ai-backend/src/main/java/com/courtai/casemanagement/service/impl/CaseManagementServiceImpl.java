package com.courtai.casemanagement.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseFlag;
import com.courtai.casefile.entity.CaseLegalInfo;
import com.courtai.casefile.entity.CaseParty;
import com.courtai.casefile.entity.SubordinateCourtDetail;
import com.courtai.casefile.entity.ActSectionDetail;
import com.courtai.casefile.repository.*;
import com.courtai.casemanagement.dto.*;
import com.courtai.casemanagement.mapper.CaseMapper;
import com.courtai.casemanagement.service.CaseManagementService;
import com.courtai.casemanagement.service.CaseTimelineService;
import com.courtai.casemanagement.service.CaseWorkflowService;
import com.courtai.casecategory.repository.CaseCategoryRepository;
import com.courtai.common.enums.CasePriority;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.PartyType;

import com.courtai.master.entity.CaseType;
import com.courtai.master.repository.CaseTypeRepository;
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
    private final CasePartyRepository    casePartyRepository;
    private final CaseLegalInfoRepository caseLegalInfoRepository;
    private final SubordinateCourtDetailRepository subordinateCourtDetailRepository;
    private final ActSectionDetailRepository actSectionDetailRepository;
    private final CourtRepository        courtRepository;
    private final CaseCategoryRepository caseCategoryRepository;
    private final CaseTypeRepository     caseTypeRepository;
    private final CaseWorkflowService    workflowService;
    private final CaseTimelineService    timelineService;
    private final CaseMapper             caseMapper;
    private final AuditService           auditService;
    private final NotificationService    notificationService;

    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseDetailsResponse createCase(CaseCreateRequest req, String actorUuid, String actorRole) {
        CaseType resolvedCaseType = null;
        if (req.getCaseType() != null && !req.getCaseType().isBlank()) {
            resolvedCaseType = caseTypeRepository.findByTypeCodeAndIsDeletedFalse(req.getCaseType())
                .orElseThrow(() -> new ResourceNotFoundException("CaseType", "typeCode", req.getCaseType()));
        }

        CaseFile caseFile = CaseFile.builder()
                .caseNumber(generateCaseNumber())
                .caseTitle(req.getCaseTitle())
                .caseDescription(req.getCaseDescription())
                .caseType(resolvedCaseType)
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

    @Override
    @Transactional
    public CaseWizardDraftResponse upsertDraft(CaseWizardDraftRequest req, String actorUuid, String actorRole) {
        CaseFile caseFile;
        boolean isNew = false;
        
        if (req.getCaseUuid() != null && !req.getCaseUuid().isBlank()) {
            caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(req.getCaseUuid())
                    .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", req.getCaseUuid()));
        } else {
            caseFile = new CaseFile();
            caseFile.setCaseNumber(generateCaseNumber());
            caseFile.setStatus(CaseStatus.DRAFT);
            caseFile.setPriority(CasePriority.LOW);
            caseFile.setFilingYear(LocalDate.now().getYear());
            caseFile.setFilingDate(LocalDate.now());
            
            // Set dummy caseType for DB constraint, will be updated later if provided
            CaseType defaultType = caseTypeRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("No case types found"));
            caseFile.setCaseType(defaultType);
            caseFile.setCaseTitle("Draft Case");
            isNew = true;
        }

        // -- Step 1 --
        if (req.getState() != null) caseFile.setState(req.getState());
        if (req.getDistrict() != null) caseFile.setDistrict(req.getDistrict());
        if (req.getCourt() != null) caseFile.setCourtEstablishment(req.getCourt());
        if (req.getCourtType() != null) caseFile.setCourtType(req.getCourtType());
        if (req.getBench() != null) caseFile.setBench(req.getBench());
        if (req.getCourtHall() != null) caseFile.setCourtHall(req.getCourtHall());
        if (req.getCaseNature() != null) caseFile.setCaseNature(req.getCaseNature());
        if (req.getFilingMode() != null) caseFile.setFilingMode(req.getFilingMode());
        if (req.getCaseType() != null) {
            caseTypeRepository.findByTypeCodeAndIsDeletedFalse(req.getCaseType())
                    .ifPresent(caseFile::setCaseType);
        }

        // -- Step 2 --
        if (req.getCaseTitle() != null) caseFile.setCaseTitle(req.getCaseTitle());
        if (req.getCauseTitle() != null) caseFile.setCauseTitle(req.getCauseTitle());
        if (req.getSubject() != null) caseFile.setSubject(req.getSubject());
        if (req.getNatureOfSuit() != null) caseFile.setNatureOfSuit(req.getNatureOfSuit());
        if (req.getShortDescription() != null) caseFile.setCaseDescription(req.getShortDescription());
        if (req.getDetailedDescription() != null) caseFile.setDetailedDescription(req.getDetailedDescription());
        if (req.getCauseOfAction() != null) caseFile.setCauseOfAction(req.getCauseOfAction());
        if (req.getDateOfCauseOfAction() != null) caseFile.setDateOfCauseAction(req.getDateOfCauseOfAction());
        if (req.getReliefSought() != null) caseFile.setReliefSought(req.getReliefSought());
        if (req.getCaseCategoryUuid() != null) caseFile.setCaseCategoryUuid(req.getCaseCategoryUuid());

        caseFile = caseFileRepository.save(caseFile);

        if (isNew) {
            CaseFlag flags = new CaseFlag();
            flags.setCaseFile(caseFile);
            caseFlagRepository.save(flags);
        }

        // -- Step 3, 4, 9 Parties --
        if (req.getPetitioner() != null) saveParty(caseFile, PartyType.PETITIONER, req.getPetitioner(), true);
        if (req.getRespondent() != null) saveParty(caseFile, PartyType.RESPONDENT, req.getRespondent(), true);
        
        if (req.getAdditionalParties() != null) {
            // Remove old additional parties to avoid dupes (simplified sync)
            casePartyRepository.findByCaseFileIdAndIsDeletedFalse(caseFile.getId()).stream()
                .filter(p -> !p.getIsPrimary())
                .forEach(casePartyRepository::delete);
            
            for (CaseWizardDraftRequest.PartyDto dto : req.getAdditionalParties()) {
                PartyType pt = PartyType.OTHER;
                if (dto.getPartyType() != null) {
                    try {
                        String normalized = dto.getPartyType().toUpperCase()
                                .replace(" ", "_")
                                .replace("-", "_")
                                .replace("POWER_OF_ATTORNEY_(POA)_HOLDER", "POA_HOLDER");
                        pt = PartyType.valueOf(normalized);
                    } catch (IllegalArgumentException e) {
                        pt = PartyType.OTHER;
                    }
                }
                saveParty(caseFile, pt, dto, false);
            }
        }

        final CaseFile finalCaseFile = caseFile;

        // -- Step 5 Legal Info --
        if (req.getPoliceStation() != null || req.getFirNumber() != null) {
            CaseLegalInfo legalInfo = caseLegalInfoRepository.findByCaseFileId(caseFile.getId())
                    .orElseGet(() -> {
                        CaseLegalInfo info = new CaseLegalInfo();
                        info.setCaseFile(finalCaseFile);
                        return info;
                    });
            if (req.getPoliceStation() != null) legalInfo.setPoliceStation(req.getPoliceStation());
            if (req.getFirNumber() != null) legalInfo.setFirNumber(req.getFirNumber());
            caseLegalInfoRepository.save(legalInfo);
        }

        // -- Step 7 Subordinate Court --
        if (req.getSubordinateCourt() != null || req.getCaseNumber() != null) {
            // Simplified: we just update the first one or create one
            SubordinateCourtDetail scd = subordinateCourtDetailRepository.findByCaseFileId(caseFile.getId())
                    .stream().findFirst().orElseGet(() -> {
                        SubordinateCourtDetail d = new SubordinateCourtDetail();
                        d.setCaseFile(finalCaseFile);
                        return d;
                    });
            if (req.getSubordinateCourt() != null) scd.setSubordinateCourt(req.getSubordinateCourt());
            if (req.getJudgeName() != null) scd.setJudgeName(req.getJudgeName());
            if (req.getCaseNumber() != null) scd.setCaseNumber(req.getCaseNumber());
            if (req.getYear() != null) scd.setYear(req.getYear());
            if (req.getCnrNumber() != null) scd.setCnrNumber(req.getCnrNumber());
            if (req.getJudgmentDate() != null) scd.setJudgmentDate(req.getJudgmentDate());
            subordinateCourtDetailRepository.save(scd);
        }

        // -- Step 8 Acts --
        if (req.getCaseActs() != null && !req.getCaseActs().isEmpty()) {
            actSectionDetailRepository.deleteAll(actSectionDetailRepository.findByCaseFileId(caseFile.getId()));
            for (CaseWizardDraftRequest.ActSectionDto dto : req.getCaseActs()) {
                ActSectionDetail act = new ActSectionDetail();
                act.setCaseFile(caseFile);
                act.setActName(dto.getAct());
                act.setSection(dto.getSection());
                act.setArticle(dto.getArticle());
                actSectionDetailRepository.save(act);
            }
        }

        return CaseWizardDraftResponse.builder()
                .caseUuid(caseFile.getUuid())
                .status("DRAFT")
                .message("Draft saved successfully")
                .build();
    }

    private void saveParty(CaseFile caseFile, PartyType type, CaseWizardDraftRequest.PartyDto dto, boolean isPrimary) {
        CaseParty party;
        if (isPrimary) {
            party = casePartyRepository.findByCaseFileIdAndIsDeletedFalse(caseFile.getId()).stream()
                    .filter(p -> p.getPartyType() == type && p.getIsPrimary())
                    .findFirst().orElseGet(() -> {
                        CaseParty p = new CaseParty();
                        p.setCaseFile(caseFile);
                        p.setPartyType(type);
                        p.setIsPrimary(true);
                        p.setPartyName(dto.getName() != null ? dto.getName() : "Unknown");
                        return p;
                    });
        } else {
            party = new CaseParty();
            party.setCaseFile(caseFile);
            party.setPartyType(type);
            party.setIsPrimary(false);
            party.setPartyName(dto.getName() != null ? dto.getName() : "Unknown");
        }

        if (dto.getName() != null) party.setPartyName(dto.getName());
        if (dto.getAlias() != null) party.setAliasName(dto.getAlias());
        if (dto.getGender() != null) party.setGender(dto.getGender());
        if (dto.getDob() != null) party.setDateOfBirth(dto.getDob());
        if (dto.getAge() != null) party.setAge(dto.getAge());
        if (dto.getOccupation() != null) party.setOccupation(dto.getOccupation());
        if (dto.getAadhaar() != null) party.setAadhaarNumber(dto.getAadhaar());
        if (dto.getPan() != null) party.setPanNumber(dto.getPan());
        if (dto.getMobile() != null) party.setPhoneNumber(dto.getMobile());
        if (dto.getEmail() != null) party.setEmail(dto.getEmail());
        if (dto.getAddress() != null) party.setPartyAddress(dto.getAddress());
        if (dto.getState() != null) party.setState(dto.getState());
        if (dto.getDistrict() != null) party.setDistrict(dto.getDistrict());
        if (dto.getPincode() != null) party.setPinCode(dto.getPincode());
        if (dto.getPartyCategory() != null) party.setPartyCategory(dto.getPartyCategory());
        if (dto.getRepresentativeName() != null) party.setRepresentativeName(dto.getRepresentativeName());
        if (dto.getPassportNumber() != null) party.setPassportNumber(dto.getPassportNumber());
        if (dto.getNationality() != null) party.setNationality(dto.getNationality());
        if (dto.getAdditionalAddress() != null) party.setAdditionalAddress(dto.getAdditionalAddress());
        if (dto.getOtherInformation() != null) party.setOtherInformation(dto.getOtherInformation());

        casePartyRepository.save(party);
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
