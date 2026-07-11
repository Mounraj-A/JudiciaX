package com.courtai.clerk.repository;

import com.courtai.clerk.entity.CaseObjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for case objections raised by clerks during scrutiny. */
@Repository
public interface CaseObjectionRepository extends JpaRepository<CaseObjection, Long> {

    List<CaseObjection> findByCaseFileUuidAndIsDeletedFalseOrderByCreatedAtDesc(String caseUuid);

    List<CaseObjection> findByCaseFileIdAndIsResolvedFalseAndIsDeletedFalse(Long caseId);

    Optional<CaseObjection> findByUuidAndIsDeletedFalse(String uuid);

    long countByCaseFileIdAndIsResolvedFalseAndIsDeletedFalse(Long caseId);
}
