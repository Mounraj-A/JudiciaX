package com.courtai.judge.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.entity.CaseStatusHistory;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.casefile.repository.CaseStatusHistoryRepository;
import com.courtai.common.enums.CaseStatus;
import com.courtai.common.enums.HearingStatus;
import com.courtai.court.entity.CourtRoom;
import com.courtai.court.repository.CourtRoomRepository;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.hearing.entity.Hearing;
import com.courtai.hearing.repository.HearingRepository;
import com.courtai.judge.dto.AdjournHearingRequest;
import com.courtai.judge.dto.JudgeHearingResponse;
import com.courtai.judge.dto.ScheduleHearingRequest;
import com.courtai.judge.dto.UpdateHearingRequest;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeHearingMapper;
import com.courtai.judge.service.JudgeHearingService;
import com.courtai.judge.service.JudgeNotificationService;
import com.courtai.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JudgeHearingService}.
 * Enforces all business rules around scheduling, adjourning, and completing hearings.
 */
@Service
@RequiredArgsConstructor
public class JudgeHearingServiceImpl implements JudgeHearingService {

    private final JudgeService                  judgeService;
    private final HearingRepository             hearingRepository;
    private final CaseFileRepository            caseFileRepository;
    private final CaseStatusHistoryRepository   statusHistoryRepository;
    private final CourtRoomRepository           courtRoomRepository;
    private final JudgeHearingMapper            hearingMapper;
    private final AuditService                  auditService;
    private final JudgeNotificationService      notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<JudgeHearingResponse> getMyHearings() {
        Judge judge = judgeService.getCurrentJudge();
        // All cases assigned to judge, then get all hearings for those cases
        List<Long> caseIds = caseFileRepository
                .findByAssignedJudgeUuidAndIsDeletedFalse(judge.getUuid(),
                        org.springframework.data.domain.Pageable.unpaged())
                .map(CaseFile::getId)
                .toList();

        return hearingRepository
                .findByCaseFileIdInAndIsDeletedFalseOrderByScheduledAtDesc(caseIds)
                .stream()
                .map(hearingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JudgeHearingResponse getHearingByUuid(String hearingUuid) {
        Judge judge   = judgeService.getCurrentJudge();
        Hearing hearing = hearingRepository.findByUuidAndIsDeletedFalse(hearingUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Hearing", "uuid", hearingUuid));
        validateHearingBelongsToJudge(hearing, judge);
        return hearingMapper.toResponse(hearing);
    }

    @Override
    @Transactional
    public JudgeHearingResponse scheduleHearing(ScheduleHearingRequest request) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(request.getCaseUuid(), judge);

        // Business rule: cannot schedule hearing for a DISPOSED case
        if (caseFile.getStatus() == CaseStatus.DISPOSED) {
            throw new BusinessRuleViolationException(
                    "Cannot schedule a hearing for a disposed case: " + request.getCaseUuid());
        }

        // Business rule: no duplicate hearing at same time in same courtroom
        if (request.getCourtRoomUuid() != null) {
            CourtRoom courtRoom = courtRoomRepository.findByUuidAndIsDeletedFalse(request.getCourtRoomUuid())
                    .orElseThrow(() -> new ResourceNotFoundException("CourtRoom", "uuid", request.getCourtRoomUuid()));
            boolean conflict = hearingRepository
                    .findByScheduledAtBetweenAndIsDeletedFalse(
                            request.getScheduledAt().minusMinutes(1),
                            request.getScheduledAt().plusMinutes(1))
                    .stream()
                    .anyMatch(h -> h.getCourtRoom() != null
                            && h.getCourtRoom().getId().equals(courtRoom.getId())
                            && h.getStatus() == HearingStatus.SCHEDULED);
            if (conflict) {
                throw new BusinessRuleViolationException(
                        "A hearing is already scheduled at this time in the same courtroom.");
            }
        }

        // Determine next hearing number for this case
        int hearingNumber = hearingRepository
                .findByCaseFileIdAndIsDeletedFalseOrderByScheduledAtDesc(caseFile.getId()).size() + 1;

        Hearing hearing = Hearing.builder()
                .caseFile(caseFile)
                .judge(judge)
                .scheduledAt(request.getScheduledAt())
                .status(HearingStatus.SCHEDULED)
                .hearingNumber(hearingNumber)
                .notes(request.getNotes())
                .isVirtual(request.getIsVirtual() != null ? request.getIsVirtual() : Boolean.FALSE)
                .build();

        if (request.getCourtRoomUuid() != null) {
            courtRoomRepository.findByUuidAndIsDeletedFalse(request.getCourtRoomUuid())
                    .ifPresent(hearing::setCourtRoom);
        }

        // Update case status to HEARING_SCHEDULED if not already progressed
        if (caseFile.getStatus() == CaseStatus.JUDGE_ASSIGNED
                || caseFile.getStatus() == CaseStatus.REGISTERED) {
            transitionStatus(caseFile, CaseStatus.HEARING_SCHEDULED, judge.getUuid());
        }

        Hearing saved = hearingRepository.save(hearing);

        auditService.logSuccess("HEARING_SCHEDULED", "Hearing", saved.getUuid(),
                "Judge " + judge.getUuid() + " scheduled hearing #" + hearingNumber
                        + " for case " + request.getCaseUuid());

        notificationService.notifyHearingScheduled(caseFile, saved);

        return hearingMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public JudgeHearingResponse updateHearing(String hearingUuid, UpdateHearingRequest request) {
        Judge   judge   = judgeService.getCurrentJudge();
        Hearing hearing = getHearingAndValidate(hearingUuid, judge);

        if (request.getScheduledAt() != null) hearing.setScheduledAt(request.getScheduledAt());
        if (request.getNotes()       != null) hearing.setNotes(request.getNotes());
        if (request.getIsVirtual()   != null) hearing.setIsVirtual(request.getIsVirtual());

        if (request.getCourtRoomUuid() != null) {
            courtRoomRepository.findByUuidAndIsDeletedFalse(request.getCourtRoomUuid())
                    .ifPresent(hearing::setCourtRoom);
        }

        Hearing saved = hearingRepository.save(hearing);

        auditService.logSuccess("HEARING_UPDATED", "Hearing", hearingUuid,
                "Judge " + judge.getUuid() + " updated hearing " + hearingUuid);

        return hearingMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public JudgeHearingResponse adjournHearing(String hearingUuid, AdjournHearingRequest request) {
        Judge   judge   = judgeService.getCurrentJudge();
        Hearing hearing = getHearingAndValidate(hearingUuid, judge);

        hearing.setStatus(HearingStatus.ADJOURNED);
        hearing.setAdjournReason(request.getAdjournReason());
        hearing.setNextHearingDate(request.getNextHearingDate());

        // Update case status to ADJOURNED
        transitionStatus(hearing.getCaseFile(), CaseStatus.ADJOURNED, judge.getUuid());

        Hearing saved = hearingRepository.save(hearing);

        auditService.logSuccess("HEARING_ADJOURNED", "Hearing", hearingUuid,
                "Judge " + judge.getUuid() + " adjourned hearing " + hearingUuid);

        notificationService.notifyHearingAdjourned(hearing.getCaseFile(), saved);

        return hearingMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public JudgeHearingResponse completeHearing(String hearingUuid) {
        Judge   judge   = judgeService.getCurrentJudge();
        Hearing hearing = getHearingAndValidate(hearingUuid, judge);

        hearing.setStatus(HearingStatus.COMPLETED);
        hearing.setActualStartAt(hearing.getScheduledAt());
        hearing.setActualEndAt(LocalDateTime.now());

        // Advance case status to IN_PROGRESS
        CaseFile caseFile = hearing.getCaseFile();
        if (caseFile.getStatus() == CaseStatus.HEARING_SCHEDULED
                || caseFile.getStatus() == CaseStatus.ADJOURNED) {
            transitionStatus(caseFile, CaseStatus.IN_PROGRESS, judge.getUuid());
        }

        Hearing saved = hearingRepository.save(hearing);

        auditService.logSuccess("HEARING_COMPLETED", "Hearing", hearingUuid,
                "Judge " + judge.getUuid() + " completed hearing " + hearingUuid);

        return hearingMapper.toResponse(saved);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CaseFile getAssignedCase(String caseUuid, Judge judge) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        if (caseFile.getAssignedJudge() == null
                || !judge.getUuid().equals(caseFile.getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException("Case is not assigned to you: " + caseUuid);
        }
        return caseFile;
    }

    private Hearing getHearingAndValidate(String hearingUuid, Judge judge) {
        Hearing hearing = hearingRepository.findByUuidAndIsDeletedFalse(hearingUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Hearing", "uuid", hearingUuid));
        validateHearingBelongsToJudge(hearing, judge);
        return hearing;
    }

    private void validateHearingBelongsToJudge(Hearing hearing, Judge judge) {
        if (hearing.getCaseFile().getAssignedJudge() == null
                || !judge.getUuid().equals(hearing.getCaseFile().getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException("This hearing does not belong to your cases.");
        }
    }

    private void transitionStatus(CaseFile caseFile, CaseStatus newStatus, String actorUuid) {
        CaseStatus oldStatus = caseFile.getStatus();
        caseFile.setStatus(newStatus);
        caseFileRepository.save(caseFile);

        CaseStatusHistory history = CaseStatusHistory.builder()
                .caseFile(caseFile)
                .fromStatus(oldStatus)
                .toStatus(newStatus)
                .changedByUuid(actorUuid)
                .changedAt(LocalDateTime.now())
                .remarks("Judge action: status transitioned to " + newStatus)
                .build();
        statusHistoryRepository.save(history);
    }
}
