package com.courtai.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * General-purpose utility methods used across the application.
 *
 * <p>All methods are static — this is a utility class, not a Spring bean.</p>
 */
@UtilityClass
public class AppUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Generates a new random UUID string.
     *
     * @return a new UUID as a string
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns the current UTC timestamp.
     *
     * @return current {@link LocalDateTime} in UTC
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Formats a {@link LocalDateTime} to the default pattern {@code yyyy-MM-dd HH:mm:ss}.
     *
     * @param dateTime the datetime to format
     * @return formatted string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Masks an email address for safe logging (e.g., {@code u***@example.com}).
     *
     * @param email the email to mask
     * @return masked email string
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        String masked = local.length() <= 2
                ? local.charAt(0) + "***"
                : local.charAt(0) + "***" + local.charAt(local.length() - 1);
        return masked + "@" + domain;
    }

    /**
     * Truncates a string to the given maximum length, appending ellipsis if truncated.
     *
     * @param text      the string to truncate
     * @param maxLength maximum allowed length
     * @return truncated string
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) return null;
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Checks if a string is null or blank.
     *
     * @param str the string to check
     * @return {@code true} if null or blank
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
