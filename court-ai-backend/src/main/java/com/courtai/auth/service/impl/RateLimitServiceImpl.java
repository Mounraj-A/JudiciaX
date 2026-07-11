package com.courtai.auth.service.impl;

import com.courtai.auth.service.RateLimitService;
import com.courtai.common.constants.SecurityConstants;
import com.courtai.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory rate limiter using a sliding window per IP per endpoint.
 *
 * <p>Uses a {@link ConcurrentHashMap} keyed by {@code "IP:path"}.
 * Window resets every 60 seconds. For production, replace with Redis.</p>
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final ConcurrentHashMap<String, RateWindow> windows = new ConcurrentHashMap<>();

    @Override
    public void checkRateLimit(HttpServletRequest request, String path) {
        String ip   = extractIp(request);
        String key  = ip + ":" + path;
        int    limit = resolveLimit(path);

        windows.compute(key, (k, window) -> {
            long now = Instant.now().getEpochSecond();
            if (window == null || now - window.windowStart() >= 60) {
                return new RateWindow(now, new AtomicInteger(1));
            }
            window.count().incrementAndGet();
            return window;
        });

        RateWindow window = windows.get(key);
        if (window != null && window.count().get() > limit) {
            log.warn("Rate limit exceeded for IP [{}] on path [{}]: {} requests/min", ip, path, window.count().get());
            throw new RateLimitExceededException(
                    "Too many requests from your IP. Please wait a minute before retrying.");
        }
    }

    private int resolveLimit(String path) {
        if (path.contains("login"))            return SecurityConstants.RATE_LIMIT_LOGIN_PER_MINUTE;
        if (path.contains("register"))         return SecurityConstants.RATE_LIMIT_REGISTER_PER_MINUTE;
        if (path.contains("forgot-password"))  return SecurityConstants.RATE_LIMIT_FORGOT_PASSWORD_PER_MINUTE;
        if (path.contains("send-otp"))         return SecurityConstants.RATE_LIMIT_FORGOT_PASSWORD_PER_MINUTE;
        return SecurityConstants.RATE_LIMIT_LOGIN_PER_MINUTE;
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    private record RateWindow(long windowStart, AtomicInteger count) {}
}
