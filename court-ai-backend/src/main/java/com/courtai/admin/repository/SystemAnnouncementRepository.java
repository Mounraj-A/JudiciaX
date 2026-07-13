package com.courtai.admin.repository;

import com.courtai.admin.entity.SystemAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemAnnouncementRepository extends JpaRepository<SystemAnnouncement, Long> {

    Optional<SystemAnnouncement> findByUuidAndIsDeletedFalse(String uuid);

    Page<SystemAnnouncement> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<SystemAnnouncement> findByIsActiveAndIsDeletedFalseOrderByCreatedAtDesc(Boolean isActive, Pageable pageable);

    /** Active announcements visible right now for a given role. */
    List<SystemAnnouncement> findByIsActiveTrueAndIsDeletedFalseAndTargetRoleInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            List<String> roles, LocalDate today, LocalDate today2);

    boolean existsByTitleAndIsDeletedFalse(String title);
}
