package com.courtai.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter — intercepts every HTTP request and validates the Bearer token.
 *
 * <p>Processing flow:</p>
 * <ol>
 *   <li>Extract Bearer token from {@code Authorization} header</li>
 *   <li>Validate token signature and expiry using {@link JwtTokenProvider}</li>
 *   <li>Load {@link UserDetails} from {@link UserDetailsService}</li>
 *   <li>Set {@link UsernamePasswordAuthenticationToken} in the {@link org.springframework.security.core.context.SecurityContext}</li>
 *   <li>Populate MDC with userId for log tracing</li>
 * </ol>
 *
 * <p>Skips authentication for requests that already have an authenticated principal
 * in the security context.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX         = "Bearer ";
    private static final String MDC_USER_ID           = "userId";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractBearerToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.extractUsername(token);

                // Only process if no existing authentication in context
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtTokenProvider.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        // Add username to MDC for downstream log correlation
                        MDC.put(MDC_USER_ID, username);

                        log.debug("JWT authentication successful for user: [{}]", username);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("JWT authentication failed: {}", ex.getMessage());
            // Do not set authentication — request proceeds as unauthenticated
        }

        filterChain.doFilter(request, response);
        MDC.remove(MDC_USER_ID);
    }

    /**
     * Extracts the raw JWT token from the Authorization header.
     *
     * @return the raw token string, or {@code null} if not present or malformed
     */
    private String extractBearerToken(HttpServletRequest request) {
        String headerValue = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(headerValue) && headerValue.startsWith(BEARER_PREFIX)) {
            String token = headerValue.substring(BEARER_PREFIX.length()).trim();
            // Swagger users sometimes paste "Bearer <token>" into a bearer-auth field,
            // which already prepends "Bearer ". Normalize that accidental double prefix.
            while (token.startsWith(BEARER_PREFIX)) {
                token = token.substring(BEARER_PREFIX.length()).trim();
            }
            return token;
        }
        return null;
    }
}
