package com.courtai.judge.service;

import com.courtai.judge.dto.JudgeNoteRequest;
import com.courtai.judge.dto.JudgeNoteResponse;

import java.util.List;

/**
 * Service contract for the judge's private notes on a case.
 * Notes are strictly private — only the owning judge can read or modify them.
 */
public interface JudgeNoteService {

    /** Creates a new private note on a case. */
    JudgeNoteResponse createNote(String caseUuid, JudgeNoteRequest request);

    /**
     * Returns all private notes for a case authored by the current judge.
     * Notes belonging to other judges are never returned.
     */
    List<JudgeNoteResponse> getNotes(String caseUuid);

    /** Updates an existing note. Validates ownership of both case and note. */
    JudgeNoteResponse updateNote(String caseUuid, String noteUuid, JudgeNoteRequest request);

    /** Soft-deletes a note. Validates ownership of both case and note. */
    void deleteNote(String caseUuid, String noteUuid);
}
