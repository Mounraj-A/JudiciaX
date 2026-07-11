package com.courtai.auth.scheduler;

import com.courtai.auth.service.SecurityEventService;
import com.courtai.common.enums.AccountStatus;
import com.courtai.common.enums.SecurityEventType;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled job to automatically unlock accounts whose timed lock has expired.
 *
 * <p>Runs every minute. Accounts locked via brute-force protection are
 * locked for {@link com.courtai.common.constants.AppConstants#ACCOUNT_LOCK_DURATION_MINUTES} minutes.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountLockScheduler {

    private final UserRepository       userRepository;
    private final SecurityEventService securityEventService;

    /**
     * Finds all accounts with expired timed locks and resets them to ACTIVE.
     * Runs every 60 seconds.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoUnlockExpiredAccounts() {
        List<User> expiredLocks = userRepository.findExpiredLockedAccounts(LocalDateTime.now());

        if (expiredLocks.isEmpty()) return;

        log.info("Auto-unlocking {} expired locked accounts", expiredLocks.size());

        expiredLocks.forEach(user -> {
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            user.setIsLocked(Boolean.FALSE);
            userRepository.save(user);

            securityEventService.record(SecurityEventType.ACCOUNT_AUTO_UNLOCKED, user,
                    "Account auto-unlocked after timed lock expiry", null, null, null, "LOW");

            log.info("Account auto-unlocked for user: [{}]", user.getEmail());
        });
    }
}
