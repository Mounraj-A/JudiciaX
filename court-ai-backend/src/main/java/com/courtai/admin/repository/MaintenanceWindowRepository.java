package com.courtai.admin.repository;

import com.courtai.admin.entity.MaintenanceWindow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceWindowRepository extends JpaRepository<MaintenanceWindow, Long> {

    Optional<MaintenanceWindow> findByUuidAndIsDeletedFalse(String uuid);

    Page<MaintenanceWindow> findByIsDeletedFalseOrderByStartTimeDesc(Pageable pageable);

    Page<MaintenanceWindow> findByStatusAndIsDeletedFalse(String status, Pageable pageable);

    /** Returns any overlapping maintenance windows — used for duplicate validation. */
    @Query("SELECT m FROM MaintenanceWindow m WHERE m.isDeleted = false AND m.status IN ('SCHEDULED','ACTIVE') " +
           "AND m.startTime < :end AND m.endTime > :start")
    List<MaintenanceWindow> findOverlapping(@Param("start") LocalDateTime start,
                                            @Param("end")   LocalDateTime end);
}
