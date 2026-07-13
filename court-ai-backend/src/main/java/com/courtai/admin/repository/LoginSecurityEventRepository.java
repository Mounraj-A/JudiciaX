package com.courtai.admin.repository;

import com.courtai.admin.entity.LoginSecurityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoginSecurityEventRepository extends JpaRepository<LoginSecurityEvent, Long> {

    Page<LoginSecurityEvent> findByEventTypeAndIsDeletedFalse(String eventType, Pageable pageable);

    Page<LoginSecurityEvent> findByUserUuidAndIsDeletedFalse(String userUuid, Pageable pageable);

    Page<LoginSecurityEvent> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(e) FROM LoginSecurityEvent e WHERE e.eventType = :type " +
           "AND e.createdAt BETWEEN :from AND :to AND e.isDeleted = false")
    long countByEventTypeAndTimeBetween(@Param("type") String type,
                                        @Param("from") LocalDateTime from,
                                        @Param("to")   LocalDateTime to);

    @Query("SELECT COUNT(e) FROM LoginSecurityEvent e WHERE e.ipAddress = :ip " +
           "AND e.createdAt > :since AND e.isDeleted = false")
    long countByIpSince(@Param("ip") String ip, @Param("since") LocalDateTime since);
}
