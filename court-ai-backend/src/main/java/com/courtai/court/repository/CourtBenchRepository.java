package com.courtai.court.repository;

import com.courtai.court.entity.CourtBench;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for CourtBench. */
@Repository
public interface CourtBenchRepository extends JpaRepository<CourtBench, Long> {

    List<CourtBench> findByCourtIdAndIsDeletedFalse(Long courtId);

    Optional<CourtBench> findByUuidAndIsDeletedFalse(String uuid);

    List<CourtBench> findByCourtIdAndBenchTypeAndIsDeletedFalse(Long courtId, String benchType);
}
