// ─── Token Validator ──────────────────────────────────────────────────────────
// Phase F2 – Validates JWT structure and expiry (not signature)
// Used by SessionRecovery and TokenService to check local token validity.

import { decodeToken } from './tokenDecoder'

// ─── Validation Result ────────────────────────────────────────────────────────
export interface TokenValidationResult {
  valid:      boolean
  reason?:    string
  expiresAt?: number    // Unix ms — for refresh scheduling
  issuedAt?:  number    // Unix ms
}

// ─── Validator ────────────────────────────────────────────────────────────────
/**
 * Validates a JWT token structure and expiry locally.
 * Does NOT verify signature — that is the backend's responsibility.
 *
 * @param token - Raw JWT string
 * @param clockSkewMs - Acceptable clock skew in ms (default 30 seconds)
 */
export function validateToken(
  token: string | null,
  clockSkewMs = 30_000
): TokenValidationResult {
  if (!token) {
    return { valid: false, reason: 'No token provided' }
  }

  if (typeof token !== 'string' || token.split('.').length !== 3) {
    return { valid: false, reason: 'Malformed token structure' }
  }

  const decoded = decodeToken(token)
  if (!decoded) {
    return { valid: false, reason: 'Token payload is not decodable' }
  }

  if (typeof decoded.exp !== 'number') {
    return { valid: false, reason: 'Token missing expiry claim' }
  }

  const expiryMs  = decoded.exp * 1000
  const issuedMs  = decoded.iat ? decoded.iat * 1000 : undefined
  const nowMs     = Date.now()

  if (nowMs >= expiryMs + clockSkewMs) {
    return {
      valid:     false,
      reason:    'Token expired',
      expiresAt: expiryMs,
      issuedAt:  issuedMs,
    }
  }

  return {
    valid:     true,
    expiresAt: expiryMs,
    issuedAt:  issuedMs,
  }
}

/** Quick boolean check — for hot paths. */
export function isTokenValid(token: string | null): boolean {
  return validateToken(token).valid
}

/** Returns remaining ms before expiry, or 0 if expired/invalid. */
export function getTokenValidityMs(token: string | null): number {
  const result = validateToken(token)
  if (!result.valid || !result.expiresAt) return 0
  return Math.max(0, result.expiresAt - Date.now())
}
