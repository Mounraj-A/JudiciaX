package com.courtai.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT token utility service for generation, validation, and claims extraction.
 *
 * <p>Uses HMAC-SHA256 signing via the configured {@code JWT_SECRET} environment variable.</p>
 *
 * <p>Token structure (claims):</p>
 * <ul>
 *   <li>{@code sub} — username (email)</li>
 *   <li>{@code roles} — list of user authority strings</li>
 *   <li>{@code iat} — issued at</li>
 *   <li>{@code exp} — expiration</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    // =========================================================
    //  TOKEN GENERATION
    // =========================================================

    /**
     * Generates an access JWT token from a Spring Security {@link Authentication} object.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return buildToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    /**
     * Generates a refresh token for a given username.
     */
    public String generateRefreshToken(String username) {
        return buildToken(new HashMap<>(), username, refreshExpirationMs);
    }

    /**
     * Generates an access token from a {@link UserDetails} object.
     */
    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return buildToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    // =========================================================
    //  TOKEN VALIDATION
    // =========================================================

    /**
     * Validates the token against the given {@link UserDetails}.
     *
     * @return {@code true} if the token is valid and not expired
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Validates the token structure and signature only (without user context).
     *
     * @return {@code true} if the token is structurally valid
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SignatureException ex) {
            log.warn("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Malformed JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    // =========================================================
    //  CLAIMS EXTRACTION
    // =========================================================

    /**
     * Extracts the username (subject) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the roles list from the token claims.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = parseClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Extracts the expiration date from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic claim extractor using a claims resolver function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(parseClaims(token));
    }

    /**
     * Returns the remaining validity time of the token in milliseconds.
     */
    public long getTokenRemainingValidity(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    // =========================================================
    //  PRIVATE HELPERS
    // =========================================================

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expirationMs) {
        Date now        = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
