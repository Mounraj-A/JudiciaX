package com.courtai.judge.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.casefile.entity.CaseFile;
import com.courtai.casefile.repository.CaseFileRepository;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.judge.dto.JudgeNoteRequest;
import com.courtai.judge.dto.JudgeNoteResponse;
import com.courtai.judge.entity.Judge;
import com.courtai.judge.mapper.JudgeNoteMapper;
import com.courtai.judge.service.JudgeNoteService;
import com.courtai.judge.service.JudgeService;
import com.courtai.note.entity.JudgeNote;
import com.courtai.note.repository.JudgeNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JudgeNoteService}.
 * Private notes are strictly scoped to the owning judge and case.
 */
@Service
@RequiredArgsConstructor
public class JudgeNoteServiceImpl implements JudgeNoteService {

    private final JudgeService          judgeService;
    private final JudgeNoteRepository   noteRepository;
    private final CaseFileRepository    caseFileRepository;
    private final JudgeNoteMapper       noteMapper;
    private final AuditService          auditService;

    @Override
    @Transactional
    public JudgeNoteResponse createNote(String caseUuid, JudgeNoteRequest request) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        JudgeNote note = JudgeNote.builder()
                .caseFile(caseFile)
                .judge(judge)
                .noteText(request.getNoteText())
                .noteType(request.getNoteType() != null ? request.getNoteType() : "OBSERVATION")
                .noteDate(LocalDate.now())
                .isConfidential(request.getIsConfidential() != null ? request.getIsConfidential() : Boolean.TRUE)
                .build();

        JudgeNote saved = noteRepository.save(note);

        auditService.logSuccess("NOTE_CREATED", "JudgeNote", saved.getUuid(),
                "Judge " + judge.getUuid() + " created note on case " + caseUuid);

        return noteMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JudgeNoteResponse> getNotes(String caseUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);

        return noteRepository
                .findByCaseFileIdAndJudgeIdAndIsDeletedFalse(caseFile.getId(), judge.getId())
                .stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JudgeNoteResponse updateNote(String caseUuid, String noteUuid, JudgeNoteRequest request) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);
        JudgeNote note    = getNoteAndValidateOwnership(noteUuid, caseFile, judge);

        if (request.getNoteText() != null)    note.setNoteText(request.getNoteText());
        if (request.getNoteType() != null)    note.setNoteType(request.getNoteType());
        if (request.getIsConfidential() != null) note.setIsConfidential(request.getIsConfidential());

        JudgeNote saved = noteRepository.save(note);

        auditService.logSuccess("NOTE_UPDATED", "JudgeNote", noteUuid,
                "Judge " + judge.getUuid() + " updated note " + noteUuid);

        return noteMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteNote(String caseUuid, String noteUuid) {
        Judge    judge    = judgeService.getCurrentJudge();
        CaseFile caseFile = getAssignedCase(caseUuid, judge);
        JudgeNote note    = getNoteAndValidateOwnership(noteUuid, caseFile, judge);

        note.softDelete();
        noteRepository.save(note);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns the case if assigned to the given judge, else throws. */
    private CaseFile getAssignedCase(String caseUuid, Judge judge) {
        CaseFile caseFile = caseFileRepository.findByUuidAndIsDeletedFalse(caseUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CaseFile", "uuid", caseUuid));
        if (caseFile.getAssignedJudge() == null
                || !judge.getUuid().equals(caseFile.getAssignedJudge().getUuid())) {
            throw new UnauthorizedActionException("You are not assigned to case: " + caseUuid);
        }
        return caseFile;
    }

    /** Returns the note if it belongs to the case and the given judge, else throws. */
    private JudgeNote getNoteAndValidateOwnership(String noteUuid, CaseFile caseFile, Judge judge) {
        JudgeNote note = noteRepository.findByUuidAndIsDeletedFalse(noteUuid)
                .orElseThrow(() -> new ResourceNotFoundException("JudgeNote", "uuid", noteUuid));
        if (!note.getCaseFile().getId().equals(caseFile.getId())
                || !note.getJudge().getId().equals(judge.getId())) {
            throw new UnauthorizedActionException("Note does not belong to you or this case: " + noteUuid);
        }
        return note;
    }
}
