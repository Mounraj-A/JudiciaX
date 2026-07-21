// ─── Audit Event Types ────────────────────────────────────────────────────────
// Phase F2 – Frontend-side audit hooks
// These events are emitted via the EventBus. In Phase F5+, they will be
// forwarded to a monitoring/audit service transport layer.

// ─── Audit Event Types ────────────────────────────────────────────────────────
export type AuditEventType =
  | 'LOGIN'
  | 'LOGOUT'
  | 'TOKEN_REFRESH'
  | 'SESSION_EXPIRED'
  | 'PERMISSION_DENIED'
  | 'UNAUTHORIZED_ACCESS'
  | 'REGISTRATION'
  | 'PASSWORD_RESET_REQUEST'
  | 'PASSWORD_RESET_COMPLETE'

// ─── Audit Context ────────────────────────────────────────────────────────────
export interface AuditContext {
  userId?:        string
  role?:          string
  timestamp:      string      // ISO 8601
  correlationId?: string      // From X-Correlation-ID request header
  path?:          string      // Browser location path
}

// ─── Frontend Audit Event ─────────────────────────────────────────────────────
/**
 * Frontend audit event — NOT persisted to backend in Phase F2.
 * Emitted via eventBus for local dev logging + future monitoring integration.
 */
export interface FrontendAuditEvent {
  type:      AuditEventType
  context:   AuditContext
  details?:  Record<string, unknown>
}
