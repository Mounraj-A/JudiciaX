package com.courtai.court.repository;

import com.courtai.court.entity.Court;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for Court — the institutional parent of all judicial proceedings. */
@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    Optional<Court> findByCourtCodeAndIsDeletedFalse(String courtCode);

    Optional<Court> findByUuidAndIsDeletedFalse(String uuid);

    Page<Court> findByStateAndIsDeletedFalse(String state, Pageable pageable);

    Page<Court> findByCourtTypeAndIsDeletedFalse(String courtType, Pageable pageable);

    boolean existsByCourtCode(String courtCode);

    long countByIsActiveTrueAndIsDeletedFalse();
}
