// ─── Session Types ────────────────────────────────────────────────────────────
// Phase F2 – Session state and metadata definitions

// ─── Session Metadata ─────────────────────────────────────────────────────────
/**
 * Frontend-side session metadata — for display and UX purposes only.
 *
 * ⚠️  SECURITY BOUNDARY: This data is NOT used as trusted audit evidence.
 *     - No client IP address (unreliable from frontend, GDPR risk)
 *     - No browser fingerprint (privacy concern, unreliable)
 *     Only lightweight, non-sensitive session context is collected.
 */
export interface SessionMetadata {
  loginTime:    string        // ISO timestamp of session start
  lastActivity: number        // Unix timestamp of last user interaction
  rememberMe:   boolean       // Storage strategy choice
  browserName:  string        // Parsed from navigator.userAgent (e.g. 'Chrome', 'Firefox')
  deviceType:   DeviceType    // Inferred from screen.width
}

export type DeviceType = 'mobile' | 'tablet' | 'desktop'

// ─── Idle State ───────────────────────────────────────────────────────────────
export interface IdleState {
  isIdle:       boolean
  idleWarning:  boolean       // True when approaching timeout (2 min warning)
  remainingMs:  number        // Time remaining before auto-logout
}

// ─── Session State ────────────────────────────────────────────────────────────
/** Redux sessionSlice shape */
export interface SessionState {
  isIdle:          boolean
  idleWarning:     boolean
  sessionExpired:  boolean
  lastActivity:    number         // Unix timestamp
  metadata:        SessionMetadata | null
}

// ─── Storage Strategy Type ────────────────────────────────────────────────────
export type StorageStrategyType = 'localStorage' | 'sessionStorage' | 'memory'
