package com.courtai.auth.repository;

import com.courtai.auth.entity.LoginHistory;
import com.courtai.common.enums.LoginStatus;
import com.courtai.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserOrderByLoginTimeDesc(User user);
    List<LoginHistory> findTop10ByUserOrderByLoginTimeDesc(User user);
    long countByLoginTimeBetween(LocalDateTime from, LocalDateTime to);
    long countByStatusAndLoginTimeBetween(LoginStatus status, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(lh) FROM LoginHistory lh WHERE lh.loginTime BETWEEN :from AND :to AND lh.status = 'FAILED'")
    long countFailedLoginsToday(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /** Check if this IP+user combo has been seen before (trusted device detection). */
    boolean existsByUserAndIpAddressAndStatus(User user, String ipAddress, LoginStatus status);
}
