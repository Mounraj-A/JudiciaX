package com.courtai.auth.repository;

import com.courtai.auth.entity.EmailVerificationToken;
import com.courtai.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
    @Modifying
    @Query("UPDATE EmailVerificationToken t SET t.used = true WHERE t.user = :user")
    void invalidateAllForUser(@Param("user") User user);
}
