package com.courtai.util;

import com.courtai.common.constants.SecurityConstants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utility for hashing token strings before persistence.
 *
 * <p>Raw JWT/refresh/reset tokens are NEVER stored in the database.
 * Only their SHA-256 hex digest is persisted, so a DB breach does not
 * expose valid tokens.</p>
 */
@Slf4j
@UtilityClass
public class TokenHashUtil {

    /**
     * Computes the SHA-256 hex digest of the given token string.
     *
     * @param rawToken the raw token value
     * @return 64-character hex digest
     */
    public static String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Token must not be null or blank");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(SecurityConstants.TOKEN_HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            log.error("SHA-256 algorithm not available: {}", ex.getMessage());
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }
}
