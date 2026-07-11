package com.courtai.auth.repository;

import com.courtai.auth.entity.SecurityEvent;
import com.courtai.common.enums.SecurityEventType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
    List<SecurityEvent> findByEventTypeOrderByEventTimeDesc(SecurityEventType eventType);
    List<SecurityEvent> findAllByOrderByEventTimeDesc(Pageable pageable);

    @Query("SELECT COUNT(se) FROM SecurityEvent se WHERE se.eventType = :type AND se.eventTime BETWEEN :from AND :to")
    long countByTypeAndTimeBetween(
            @Param("type") SecurityEventType type,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(se) FROM SecurityEvent se WHERE se.severity = :severity AND se.eventTime > :since")
    long countHighSeverityEventsSince(@Param("severity") String severity, @Param("since") LocalDateTime since);
}
