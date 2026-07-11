package com.courtai.auth.repository;

import com.courtai.auth.entity.MobileOtp;
import com.courtai.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileOtpRepository extends JpaRepository<MobileOtp, Long> {
    Optional<MobileOtp> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);
    Optional<MobileOtp> findTopByPhoneNumberAndUsedFalseOrderByCreatedAtDesc(String phoneNumber);
    @Modifying
    @Query("UPDATE MobileOtp o SET o.used = true WHERE o.user = :user")
    void invalidateAllForUser(@Param("user") User user);
}
