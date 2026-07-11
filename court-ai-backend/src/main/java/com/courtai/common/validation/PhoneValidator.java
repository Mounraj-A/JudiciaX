package com.courtai.common.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Reusable phone number validator.
 *
 * <p>Accepts exactly 10 numeric digits (Indian mobile number format).
 * Does not validate country codes — add them separately if international support is needed.</p>
 */
@Component
public class PhoneValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    /**
     * Validates an Indian mobile phone number.
     *
     * @param phone the phone number string to validate
     * @return {@code true} if the number is exactly 10 digits starting with 6–9
     */
    public boolean isValid(String phone) {
        if (phone == null || phone.isBlank()) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Strips any non-digit characters and returns the cleaned phone number.
     *
     * @param phone raw input (may contain spaces or dashes)
     * @return digits-only phone number
     */
    public String normalise(String phone) {
        if (phone == null) return null;
        return phone.trim().replaceAll("[^0-9]", "");
    }
}
