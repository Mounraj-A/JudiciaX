// ─── Event Bus Types ──────────────────────────────────────────────────────────
// Phase F2 — Extended with audit events

export type AppEventType =
  // Theme events
  | 'theme:change'
  | 'theme:density-change'
  // Auth events
  | 'auth:login'
  | 'auth:logout'
  | 'auth:token-refresh'
  | 'auth:session-expired'
  | 'auth:unauthorized'
  // Navigation events
  | 'navigation:route-change'
  | 'navigation:breadcrumb-update'
  | 'navigation:sidebar-toggle'
  // Notification events
  | 'notification:show'
  | 'notification:dismiss'
  | 'notification:clear-all'
  // Session events
  | 'session:idle-warning'
  | 'session:idle-timeout'
  | 'session:active'
  // API events
  | 'api:request-start'
  | 'api:request-end'
  | 'api:error'
  // App events
  | 'app:ready'
  | 'app:error'
  // Audit events (frontend hooks — emitted locally, Phase F5+ sends to monitoring)
  | 'audit:login'
  | 'audit:logout'
  | 'audit:token-refresh'
  | 'audit:session-expired'
  | 'audit:permission-denied'
  | 'audit:unauthorized-access'
  | 'audit:registration'
  | 'audit:password-reset'

export interface AppEvent<T = unknown> {
  type:      AppEventType
  payload?:  T
  timestamp: number
  source?:   string
}

export type EventHandler<T = unknown> = (event: AppEvent<T>) => void
