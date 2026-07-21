package com.courtai.note.repository;

import com.courtai.note.entity.AdvocateCaseNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvocateCaseNoteRepository extends JpaRepository<AdvocateCaseNote, Long> {

    Page<AdvocateCaseNote> findByCaseFileUuidAndAdvocateUuidAndIsDeletedFalseOrderByCreatedAtDesc(String caseUuid, String advocateUuid, Pageable pageable);

    Optional<AdvocateCaseNote> findByUuidAndAdvocateUuidAndIsDeletedFalse(String uuid, String advocateUuid);
}
