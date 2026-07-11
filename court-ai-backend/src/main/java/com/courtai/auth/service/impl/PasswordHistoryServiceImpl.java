package com.courtai.auth.service.impl;

import com.courtai.auth.entity.PasswordHistory;
import com.courtai.auth.repository.PasswordHistoryRepository;
import com.courtai.auth.service.PasswordHistoryService;
import com.courtai.common.constants.AppConstants;
import com.courtai.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public boolean isPasswordReused(User user, String rawPassword) {
        List<PasswordHistory> history = passwordHistoryRepository.findRecentByUser(
                user, PageRequest.of(0, AppConstants.PASSWORD_HISTORY_COUNT));
        return history.stream()
                .anyMatch(ph -> passwordEncoder.matches(rawPassword, ph.getPasswordHash()));
    }

    @Override
    @Transactional
    public void saveToHistory(User user, String passwordHash) {
        PasswordHistory entry = PasswordHistory.builder()
                .user(user)
                .passwordHash(passwordHash)
                .changedAt(LocalDateTime.now())
                .build();
        passwordHistoryRepository.save(entry);

        // Trim history to last N entries
        List<PasswordHistory> all = passwordHistoryRepository.findRecentByUser(
                user, PageRequest.of(0, Integer.MAX_VALUE));
        if (all.size() > AppConstants.PASSWORD_HISTORY_COUNT) {
            List<PasswordHistory> toDelete = all.subList(AppConstants.PASSWORD_HISTORY_COUNT, all.size());
            passwordHistoryRepository.deleteAll(toDelete);
        }
        log.debug("Password history updated for user: {}", user.getEmail());
    }
}
