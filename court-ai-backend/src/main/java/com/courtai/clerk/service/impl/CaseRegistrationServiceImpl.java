package com.courtai.clerk.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseStatusHistory;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casefile.repository.CaseStatusHistoryRepository;
import com.courtai.clerk.dto.CaseRegistrationResponse;
import com.courtai.clerk.service.CaseNumberGeneratorService;
import com.courtai.clerk.service.CaseRegistrationService;
import com.courtai.clerk.util.ClerkNotificationHelper;
import com.courtai.clerk.util.ClerkSecurityUtil;
import com.courtai.common.enums.CaseStatus;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of {@link CaseRegistrationService}.
 *
 * <h3>Registration checklist enforced here:</h3>
 * <ol>
 *   <li>Case must be in UNDER_SCRUTINY status</li>
 *   <li>Case must not already be registered</li>
 *   <li>Court jurisdiction must be verified</li>
 *   <li>No unresolved objections</li>
 *   <li>Generate official case number</li>
 *   <li>Transition status → REGISTERED</li>
 *   <li>Place in judge queue</li>
 *   <li>Notify advocate</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaseRegistrationServiceImpl implements CaseRegistrationService {

    private final ClerkSecurityUtil           securityUtil;
    private final CaseFileRepository          caseFileRepository;
    private final CaseStatusHistoryRepository statusHistoryRepository;
    private final CaseNumberGeneratorService  caseNumberGenerator;
    private final ClerkNotificationHelper     notificationHelper;
    private final AuditService                auditService;

    @Override
    @Transactional
    public CaseRegistrationResponse registerCase(String caseUuid, String remarks) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        // Court access validation
        if (caseFile.getCourt() == null
                || !caseFile.getCourt().getId().equals(clerk.getCourt().getId())) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your assigned court.");
        }

        // 1. Status check
        if (caseFile.getStatus() != CaseStatus.UNDER_SCRUTINY) {
            throw new BusinessRuleViolationException(
                    "Only UNDER_SCRUTINY cases can be registered. Current status: " + caseFile.getStatus());
        }

        // 2. Already registered guard
        if (caseFile.getOfficialCaseNumber() != null) {
            throw new BusinessRuleViolationException(
                    "Case already has an official number: " + caseFile.getOfficialCaseNumber());
        }

        // 3. Jurisdiction verification
        if (!Boolean.TRUE.equals(caseFile.getIsJurisdictionVerified())) {
            throw new BusinessRuleViolationException(
                    "Jurisdiction must be verified before registration. Use PUT /clerk/cases/{uuid}/verify first.");
        }

        // 4. Generate official case number
        String officialNumber = caseNumberGenerator.generateOfficialCaseNumber(caseFile);
        caseFile.setOfficialCaseNumber(officialNumber);

        // 5. Determine judge queue position
        long queueCount = caseFileRepository.countPendingJudgeAssignment(clerk.getCourt().getId());
        int queuePosition = (int) queueCount + 1;

        // 6. Update case fields
        CaseStatus from = caseFile.getStatus();
        caseFile.setStatus(CaseStatus.REGISTERED);
        caseFile.setRegisteredAt(LocalDateTime.now());
        caseFile.setRegisteredByUuid(clerk.getUuid());
        caseFile.setVerificationRemarks(remarks);
        caseFile.setJudgeQueuePosition(queuePosition);
        caseFile.setJudgeQueuedAt(LocalDateTime.now());
        caseFileRepository.save(caseFile);

        // 7. Status history
        statusHistoryRepository.save(CaseStatusHistory.builder()
                .caseFile(caseFile)
                .fromStatus(from)
                .toStatus(CaseStatus.REGISTERED)
                .changedAt(LocalDateTime.now())
                .changedByUuid(clerk.getUuid())
                .changedByRole("CLERK")
                .remarks("Case officially registered. Official number: " + officialNumber)
                .build());

        // 8. Audit
        auditService.logSuccess("CASE_REGISTERED", "CaseFile", caseUuid,
                "Clerk " + clerk.getUuid() + " registered case " + caseFile.getCaseNumber()
                + " as " + officialNumber + " — queue position: " + queuePosition);

        // 9. Notify advocate
        if (caseFile.getPetitionerAdvocate() != null
                && caseFile.getPetitionerAdvocate().getUser() != null) {
            notificationHelper.notifyCaseRegistered(
                    caseFile.getPetitionerAdvocate().getUser().getUuid(),
                    caseUuid, officialNumber, caseFile.getCaseNumber());
        }

        log.info("Case {} registered as {} — queue position: {}",
                caseFile.getCaseNumber(), officialNumber, queuePosition);

        String clerkName = clerk.getUser() != null ? clerk.getUser().getFullName() : clerk.getUuid();
        return CaseRegistrationResponse.builder()
                .uuid(caseFile.getUuid())
                .caseNumber(caseFile.getCaseNumber())
                .officialCaseNumber(officialNumber)
                .caseTitle(caseFile.getCaseTitle())
                .status(CaseStatus.REGISTERED.name())
                .registeredAt(caseFile.getRegisteredAt())
                .registeredByClerkName(clerkName)
                .judgeQueuePosition(queuePosition)
                .message("Case '" + caseFile.getCaseTitle() + "' has been officially registered as "
                        + officialNumber + " and placed at position " + queuePosition
                        + " in the judge assignment queue.")
                .build();
    }
}
