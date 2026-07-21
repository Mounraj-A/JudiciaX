// ─── Audit Types — Phase F3 ───────────────────────────────────────────────────

export type AuditSeverity  = 'info' | 'warning' | 'error' | 'critical'
export type AuditEventType =
  | 'CREATE' | 'READ' | 'UPDATE' | 'DELETE' | 'EXPORT'
  | 'LOGIN' | 'LOGOUT' | 'AUTH_FAILURE' | 'PERMISSION_DENIED'
  | 'APPROVE' | 'REJECT' | 'SUBMIT' | 'WITHDRAW'
  | 'AI_QUERY' | 'AI_OVERRIDE' | 'AI_APPROVE'
  | 'SYSTEM' | 'CONFIG_CHANGE' | 'IMPORT'

export interface AuditActor {
  id:     string
  name:   string
  role:   string
  email?: string
}

export interface AuditEvent {
  id:          string
  eventType:   AuditEventType
  severity:    AuditSeverity
  actor:       AuditActor
  resource:    string      // resource type e.g. 'case', 'document'
  resourceId?: string
  action:      string      // human description e.g. 'Updated case status to CLOSED'
  timestamp:   string      // ISO
  ipAddress?:  string
  userAgent?:  string
  before?:     Record<string, unknown>  // snapshot before change
  after?:      Record<string, unknown>  // snapshot after change
  metadata?:   Record<string, unknown>
  traceId?:    string      // correlation ID
}

export interface AuditFilter {
  eventTypes?: AuditEventType[]
  severity?:   AuditSeverity[]
  actorId?:    string
  resource?:   string
  dateFrom?:   string
  dateTo?:     string
  traceId?:    string
}

export interface AuditState {
  events:     AuditEvent[]
  filter:     AuditFilter
  isLoading:  boolean
  error?:     string | null
}
