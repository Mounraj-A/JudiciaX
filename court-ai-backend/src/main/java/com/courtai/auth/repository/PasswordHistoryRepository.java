package com.courtai.auth.repository;

import com.courtai.auth.entity.PasswordHistory;
import com.courtai.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    /** Returns last N password hashes for this user, newest first. */
    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.user = :user ORDER BY ph.changedAt DESC")
    List<PasswordHistory> findRecentByUser(@Param("user") User user, org.springframework.data.domain.Pageable pageable);
}
