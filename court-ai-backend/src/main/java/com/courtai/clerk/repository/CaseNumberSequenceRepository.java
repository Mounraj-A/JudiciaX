package com.courtai.clerk.repository;

import com.courtai.clerk.entity.CaseNumberSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for case number sequence counters. */
@Repository
public interface CaseNumberSequenceRepository extends JpaRepository<CaseNumberSequence, Long> {

    /**
     * Retrieves the sequence with a PESSIMISTIC_WRITE lock to prevent
     * concurrent threads from generating duplicate case numbers.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM CaseNumberSequence s WHERE s.court.id = :courtId AND s.year = :year")
    Optional<CaseNumberSequence> findByCourtIdAndYearForUpdate(
            @Param("courtId") Long courtId,
            @Param("year") Integer year);
}
