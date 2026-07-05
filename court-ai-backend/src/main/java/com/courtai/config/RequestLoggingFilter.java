package com.courtai.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * HTTP Request/Response logging filter.
 *
 * <p>Executes once per request and logs:</p>
 * <ul>
 *   <li>HTTP method, URI, query string, client IP</li>
 *   <li>Response status code and processing time</li>
 *   <li>Correlation ID injected into MDC for log tracing</li>
 * </ul>
 *
 * <p>MDC keys populated:</p>
 * <ul>
 *   <li>{@code requestId} — UUID per request for log correlation</li>
 *   <li>{@code clientIp} — originating client IP address</li>
 * </ul>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID    = "requestId";
    private static final String MDC_CLIENT_IP     = "clientIp";

    // Skip logging for these paths (health checks, static resources)
    private static final String[] SKIP_PATHS = {
            "/actuator/health",
            "/actuator/info",
            "/favicon.ico"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String skipPath : SKIP_PATHS) {
            if (uri.contains(skipPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Generate or extract correlation ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        String clientIp = resolveClientIp(request);

        // Populate MDC for downstream log statements
        MDC.put(MDC_REQUEST_ID, requestId);
        MDC.put(MDC_CLIENT_IP, clientIp);

        // Propagate request ID in the response
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            log.info("→ [{} {}{}] from [{}] requestId=[{}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    query,
                    clientIp,
                    requestId);

            filterChain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            log.info("← [{} {}] status=[{}] duration=[{}ms] requestId=[{}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    requestId);

            // Always clear MDC to prevent thread pool leaks
            MDC.remove(MDC_REQUEST_ID);
            MDC.remove(MDC_CLIENT_IP);
        }
    }

    /**
     * Resolves the true client IP, respecting X-Forwarded-For headers from proxies/load balancers.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For can be a comma-separated list; first IP is the original client
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
