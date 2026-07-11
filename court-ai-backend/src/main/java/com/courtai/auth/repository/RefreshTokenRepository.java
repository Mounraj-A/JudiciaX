package com.courtai.auth.repository;

import com.courtai.auth.entity.RefreshToken;
import com.courtai.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.used = true WHERE rt.user = :user")
    void revokeAllByUser(@Param("user") User user);
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.used = true WHERE rt.sessionId = :sessionId")
    void revokeBySessionId(@Param("sessionId") String sessionId);
}
