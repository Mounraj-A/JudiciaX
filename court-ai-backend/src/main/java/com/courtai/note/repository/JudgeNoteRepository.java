package com.courtai.note.repository;

import com.courtai.note.entity.JudgeNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for JudgeNote — private judicial observations on a case. */
@Repository
public interface JudgeNoteRepository extends JpaRepository<JudgeNote, Long> {

    List<JudgeNote> findByCaseFileIdAndIsDeletedFalseOrderByNoteDateDesc(Long caseFileId);

    List<JudgeNote> findByCaseFileIdAndJudgeIdAndIsDeletedFalse(Long caseFileId, Long judgeId);

    /** Returns only non-confidential notes (visible to authorised parties). */
    List<JudgeNote> findByCaseFileIdAndIsConfidentialFalseAndIsDeletedFalse(Long caseFileId);

    Optional<JudgeNote> findByUuidAndIsDeletedFalse(String uuid);
}
