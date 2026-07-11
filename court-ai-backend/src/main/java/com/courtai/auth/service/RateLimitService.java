package com.courtai.auth.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * In-memory rate limiting service — limits requests per IP per minute for auth endpoints.
 */
public interface RateLimitService {
    /**
     * Checks whether the given IP has exceeded the rate limit for the given endpoint path.
     *
     * @throws com.courtai.exception.RateLimitExceededException if limit exceeded
     */
    void checkRateLimit(HttpServletRequest request, String path);
}
