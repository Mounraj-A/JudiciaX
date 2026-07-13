package com.courtai.casemanagement.service.impl;

import com.courtai.advocate.entity.Advocate;
import com.courtai.advocate.repository.AdvocateRepository;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casemanagement.service.CaseOwnershipService;
import com.courtai.clerk.entity.Clerk;
import com.courtai.clerk.repository.ClerkRepository;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.repository.JudgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseOwnershipServiceImpl implements CaseOwnershipService {

    private final CaseFileRepository    caseFileRepository;
    private final AdvocateRepository    advocateRepository;
    private final JudgeRepository       judgeRepository;
    private final ClerkRepository       clerkRepository;

    @Override
    public void verifyAdvocateOwnership(String caseUuid, String advocateUuid) {
        CaseFile caseFile = loadCase(caseUuid);
        Advocate advocate = advocateRepository.findByUuidAndIsDeletedFalse(advocateUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Advocate", "uuid", advocateUuid));

        boolean isPetitionerAdvocate = caseFile.getPetitionerAdvocate() != null
                && caseFile.getPetitionerAdvocate().getId().equals(advocate.getId());
        boolean isRespondentAdvocate = caseFile.getRespondentAdvocate() != null
                && caseFile.getRespondentAdvocate().getId().equals(advocate.getId());

        if (!isPetitionerAdvocate && !isRespondentAdvocate) {
            throw new UnauthorizedActionException(
                    "Advocate " + advocateUuid + " does not have ownership of case " + caseUuid);
        }
    }

    @Override
    public void verifyJudgeAssignment(String caseUuid, String judgeUserUuid) {
        CaseFile caseFile = loadCase(caseUuid);
        Judge judge = judgeRepository.findByUserUuidAndIsDeletedFalse(judgeUserUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Judge", "userUuid", judgeUserUuid));

        if (caseFile.getAssignedJudge() == null
                || !caseFile.getAssignedJudge().getId().equals(judge.getId())) {
            throw new UnauthorizedActionException(
                    "Judge " + judgeUserUuid + " is not assigned to case " + caseUuid);
        }
    }

    @Override
    public void verifyClerkCourt(String caseUuid, String clerkUserUuid) {
        CaseFile caseFile = loadCase(caseUuid);
        Clerk clerk = clerkRepository.findByUserUuidAndIsDeletedFalse(clerkUserUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Clerk", "userUuid", clerkUserUuid));

        if (caseFile.getCourt() == null || clerk.getCourt() == null
                || !caseFile.getCourt().getId().equals(clerk.getCourt().getId())) {
            throw new UnauthorizedActionException(
                    "Clerk " + clerkUserUuid + " is not assigned to the court of case " + caseUuid);
        }
    }

    @Override
    public boolean isAdmin(String actorRole) {
        return "ROLE_ADMIN".equalsIgnoreCase(actorRole);
    }

    @Override
    public boolean hasAccessToCase(String caseUuid, String actorUuid, String actorRole) {
        if (isAdmin(actorRole)) return true;
        try {
            if ("ROLE_ADVOCATE".equalsIgnoreCase(actorRole)) {
                Advocate advocate = advocateRepository.findByUserUuidAndIsDeletedFalse(actorUuid)
                        .orElse(null);
                if (advocate == null) return false;
                verifyAdvocateOwnership(caseUuid, advocate.getUuid());
                return true;
            }
            if ("ROLE_JUDGE".equalsIgnoreCase(actorRole)) {
                verifyJudgeAssignment(caseUuid, actorUuid);
                return true;
            }
            if ("ROLE_CLERK".equalsIgnoreCase(actorRole)) {
                verifyClerkCourt(caseUuid, actorUuid);
                return true;
            }
        } catch (UnauthorizedActionException | ResourceNotFoundException e) {
            return false;
        }
        return false;
    }

    private CaseFile loadCase(String caseUuid) {
        return caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
    }
}
