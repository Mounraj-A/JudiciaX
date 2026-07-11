package com.courtai.court.repository;

import com.courtai.court.entity.CourtRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for CourtRoom. */
@Repository
public interface CourtRoomRepository extends JpaRepository<CourtRoom, Long> {

    List<CourtRoom> findByCourtIdAndIsDeletedFalse(Long courtId);

    Optional<CourtRoom> findByUuidAndIsDeletedFalse(String uuid);

    List<CourtRoom> findByCourtIdAndIsActiveTrue(Long courtId);
}
