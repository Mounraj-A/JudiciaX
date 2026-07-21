// ─── JWT Token Decoder ────────────────────────────────────────────────────────
// Phase F2 – Lightweight, zero-dependency JWT decoder
//
// ⚠️  SECURITY BOUNDARY — READ CAREFULLY:
//     Decoded token data is for UI DISPLAY PURPOSES ONLY.
//     - Showing token expiry in UI ("Session expires in 10 minutes")
//     - Displaying role label in header
//     - Scheduling proactive refresh via DynamicRefreshTimer
//
//     Decoded data is NEVER used as the source of authorization decisions.
//     Authorization always flows through: permissionSlice ← ROLE_REGISTRY ← backend validation.

// ─── Decoded Token Shape ──────────────────────────────────────────────────────
export interface DecodedToken {
  sub:          string           // Subject — user ID or email (display only)
  exp:          number           // Expiry timestamp (Unix seconds) — for timer scheduling
  iat:          number           // Issued at (Unix seconds) — for display
  role?:        string           // Role claim — UI display only, NOT used for authorization
  permissions?: string[]         // Permission claims — UI display only, NOT authoritative
  jti?:         string           // JWT ID (for deduplication display)
  email?:       string
  name?:        string
}

// ─── Decoder ─────────────────────────────────────────────────────────────────
/**
 * Decode a JWT without signature verification.
 * Returns null on any decode error — never throws.
 *
 * @param token - Raw JWT string
 * @returns DecodedToken for UI use, or null if invalid structure
 *
 * @see {@link https://jwt.io} for JWT format reference
 */
export function decodeToken(token: string | null): DecodedToken | null {
  if (!token) return null

  try {
    const parts = token.split('.')
    if (parts.length !== 3) return null

    const payload = parts[1]
    // Base64url → Base64 → JSON
    const padded  = payload.replace(/-/g, '+').replace(/_/g, '/')
    const padLen  = (4 - (padded.length % 4)) % 4
    const base64  = padded + '='.repeat(padLen)
    const json    = atob(base64)
    const decoded = JSON.parse(json) as DecodedToken

    if (typeof decoded !== 'object' || decoded === null) return null
    if (typeof decoded.exp !== 'number') return null

    return decoded
  } catch {
    return null
  }
}

// ─── Claim Extractors ─────────────────────────────────────────────────────────
/** Get expiry timestamp in ms — for DynamicRefreshTimer. Returns null if not decodable. */
export function getTokenExpiryMs(token: string | null): number | null {
  const decoded = decodeToken(token)
  return decoded ? decoded.exp * 1000 : null
}

/** Get remaining validity duration in ms — for UI display only. */
export function getTokenRemainingMs(token: string | null): number | null {
  const expiryMs = getTokenExpiryMs(token)
  if (expiryMs === null) return null
  return Math.max(0, expiryMs - Date.now())
}

/** Get role from token — for UI display only. NOT used for authorization. */
export function getTokenRole(token: string | null): string | null {
  return decodeToken(token)?.role ?? null
}
