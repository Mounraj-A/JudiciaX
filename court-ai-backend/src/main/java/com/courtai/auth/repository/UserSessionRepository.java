package com.courtai.auth.repository;

import com.courtai.auth.entity.UserSession;
import com.courtai.common.enums.SessionStatus;
import com.courtai.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserAndStatusOrderByLoginTimeDesc(User user, SessionStatus status);
    Optional<UserSession> findByUuidAndUser(String uuid, User user);
    long countByUserAndStatus(User user, SessionStatus status);
    Optional<UserSession> findByAccessTokenHash(String accessTokenHash);

    @Modifying
    @Query("UPDATE UserSession s SET s.status = 'REVOKED', s.logoutTime = CURRENT_TIMESTAMP WHERE s.user = :user AND s.status = 'ACTIVE'")
    void revokeAllActiveSessionsForUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE UserSession s SET s.status = :status, s.logoutTime = CURRENT_TIMESTAMP WHERE s.uuid = :uuid")
    void updateStatusByUuid(@Param("uuid") String uuid, @Param("status") SessionStatus status);

    long countByStatus(SessionStatus status);
}
