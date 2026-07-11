package com.courtai.common.validation;

import com.courtai.common.constants.AppConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reusable password policy validator.
 *
 * <p>Enforces the password policy defined in {@link AppConstants}:</p>
 * <ul>
 *   <li>Minimum {@value AppConstants#PASSWORD_MIN_LENGTH} characters</li>
 *   <li>Maximum {@value AppConstants#PASSWORD_MAX_LENGTH} characters</li>
 *   <li>At least one uppercase letter</li>
 *   <li>At least one lowercase letter</li>
 *   <li>At least one digit</li>
 *   <li>At least one special character (@$!%*?&amp;#^)</li>
 *   <li>Must not contain the user's username/email local part</li>
 * </ul>
 */
@Component
public class PasswordValidator {

    private static final Pattern UPPERCASE  = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE  = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT      = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL    = Pattern.compile(".*[@$!%*?&#^()\\-_=+].*");

    /**
     * Validates a password against the full policy.
     *
     * @param password the raw password
     * @param username the user's username or email local-part (used to check containment)
     * @return list of policy violations; empty list means the password is valid
     */
    public List<String> validate(String password, String username) {
        List<String> violations = new ArrayList<>();

        if (password == null || password.isBlank()) {
            violations.add("Password must not be empty");
            return violations;
        }

        if (password.length() < AppConstants.PASSWORD_MIN_LENGTH) {
            violations.add("Password must be at least " + AppConstants.PASSWORD_MIN_LENGTH + " characters long");
        }

        if (password.length() > AppConstants.PASSWORD_MAX_LENGTH) {
            violations.add("Password must not exceed " + AppConstants.PASSWORD_MAX_LENGTH + " characters");
        }

        if (!UPPERCASE.matcher(password).matches()) {
            violations.add("Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE.matcher(password).matches()) {
            violations.add("Password must contain at least one lowercase letter");
        }

        if (!DIGIT.matcher(password).matches()) {
            violations.add("Password must contain at least one digit");
        }

        if (!SPECIAL.matcher(password).matches()) {
            violations.add("Password must contain at least one special character (@$!%*?&#^)");
        }

        if (username != null && !username.isBlank()) {
            String localPart = username.contains("@")
                    ? username.substring(0, username.indexOf('@')).toLowerCase()
                    : username.toLowerCase();
            if (password.toLowerCase().contains(localPart)) {
                violations.add("Password must not contain your username or email");
            }
        }

        return violations;
    }

    /**
     * Convenience method that returns {@code true} if the password satisfies all policy rules.
     *
     * @param password the raw password
     * @param username the user's username or email
     * @return {@code true} if valid
     */
    public boolean isValid(String password, String username) {
        return validate(password, username).isEmpty();
    }
}
