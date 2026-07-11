package com.courtai.common.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Reusable email validation utility.
 *
 * <p>Centralises email format checking so that services and request DTOs
 * rely on a single consistent rule rather than duplicating regex patterns.</p>
 */
@Component
public class EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * Checks whether the given value is a valid email address.
     *
     * @param email the email string to validate
     * @return {@code true} if valid
     */
    public boolean isValid(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Normalises an email address to lowercase and trims whitespace.
     *
     * @param email raw email input
     * @return normalised email
     */
    public String normalise(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }
}
