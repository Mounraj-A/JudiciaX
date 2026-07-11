package com.courtai.auth.service;

import com.courtai.auth.entity.PasswordHistory;
import com.courtai.user.entity.User;

/**
 * Service for enforcing password history policy.
 *
 * <p>Prevents users from reusing their last N passwords.</p>
 */
public interface PasswordHistoryService {

    /** Checks whether the given raw password matches any of the user's last N stored hashes. */
    boolean isPasswordReused(User user, String rawPassword);

    /** Saves the given hash to history and trims old entries beyond the history limit. */
    void saveToHistory(User user, String passwordHash);
}
