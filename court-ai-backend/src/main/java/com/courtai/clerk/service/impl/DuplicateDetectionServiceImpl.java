package com.courtai.clerk.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.clerk.dto.DuplicateCheckResponse;
import com.courtai.clerk.service.DuplicateDetectionService;
import com.courtai.clerk.util.ClerkNotificationHelper;
import com.courtai.clerk.util.ClerkSecurityUtil;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** Implementation of {@link DuplicateDetectionService}. */
@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateDetectionServiceImpl implements DuplicateDetectionService {

    private final ClerkSecurityUtil       securityUtil;
    private final CaseFileRepository      caseFileRepository;
    private final AuditService            auditService;
    private final ClerkNotificationHelper notificationHelper;

    @Override
    @Transactional
    public DuplicateCheckResponse checkForDuplicates(String caseUuid) {
        var clerk = securityUtil.getCurrentClerk();
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));

        // Court access enforcement
        if (caseFile.getCourt() == null
                || !caseFile.getCourt().getId().equals(clerk.getCourt().getId())) {
            throw new UnauthorizedActionException(
                    "Case '" + caseUuid + "' does not belong to your assigned court.");
        }

        String petitioner = nullSafe(caseFile.getPetitionerName());
        String respondent = nullSafe(caseFile.getRespondentName());

        List<CaseFile> potentials = caseFileRepository.findPotentialDuplicates(
                caseFile.getCourt().getId(),
                caseFile.getCaseType(),
                petitioner.length() >= 3 ? petitioner.substring(0, Math.min(petitioner.length(), 10)) : petitioner,
                respondent.length() >= 3 ? respondent.substring(0, Math.min(respondent.length(), 10)) : respondent,
                caseUuid);

        boolean found = !potentials.isEmpty();

        // Store comma-separated UUIDs on the case
        String uuidList = potentials.stream().map(CaseFile::getUuid).collect(Collectors.joining(","));
        caseFile.setIsDuplicateChecked(Boolean.TRUE);
        caseFile.setDuplicateCaseUuids(found ? uuidList : null);
        caseFileRepository.save(caseFile);

        auditService.logSuccess("DUPLICATE_CHECK", "CaseFile", caseUuid,
                "Clerk " + clerk.getUuid() + " ran duplicate check on case " + caseFile.getCaseNumber()
                + " — found " + potentials.size() + " potential duplicate(s)");

        // Notify advocate of duplicate warning
        if (found && caseFile.getPetitionerAdvocate() != null
                && caseFile.getPetitionerAdvocate().getUser() != null) {
            notificationHelper.notifyDuplicateWarning(
                    caseFile.getPetitionerAdvocate().getUser().getUuid(),
                    caseUuid, caseFile.getCaseNumber());
        }

        List<DuplicateCheckResponse.DuplicateCaseSummary> summaries = potentials.stream()
                .map(d -> DuplicateCheckResponse.DuplicateCaseSummary.builder()
                        .uuid(d.getUuid())
                        .caseNumber(d.getCaseNumber())
                        .officialCaseNumber(d.getOfficialCaseNumber())
                        .caseTitle(d.getCaseTitle())
                        .petitionerName(d.getPetitionerName())
                        .respondentName(d.getRespondentName())
                        .status(d.getStatus() != null ? d.getStatus().name() : null)
                        .filingDate(d.getFilingDate() != null ? d.getFilingDate().toString() : null)
                        .build())
                .toList();

        return DuplicateCheckResponse.builder()
                .caseUuid(caseUuid)
                .caseNumber(caseFile.getCaseNumber())
                .duplicatesFound(found)
                .duplicateCount(potentials.size())
                .potentialDuplicates(summaries)
                .message(found
                        ? potentials.size() + " potential duplicate case(s) found. Clerk decision required."
                        : "No duplicate cases found. Safe to proceed with registration.")
                .build();
    }

    private String nullSafe(String v) {
        return v != null ? v.trim() : "";
    }
}
