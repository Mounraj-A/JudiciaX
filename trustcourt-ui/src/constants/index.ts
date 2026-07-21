// ─── Permission Constants ─────────────────────────────────────────────────────
import type { PermissionString } from '@/types/permissions'

export const PERMISSIONS = {
  // Cases (Advocate/Clerk)
  CASE_VIEW:     'VIEW_CASE_DETAILS' as PermissionString,
  CASE_CREATE:   'CREATE_CASE'       as PermissionString,
  CASE_EDIT:     'edit:case'         as PermissionString, // Not used in DB yet
  CASE_DELETE:   'delete:case'       as PermissionString, // Not used in DB yet
  CASE_ASSIGN:   'ASSIGN_JUDGE'      as PermissionString,
  // Documents
  DOC_VIEW:      'MANAGE_DOCUMENTS'  as PermissionString, // Or UPLOAD_DOCUMENT
  DOC_CREATE:    'UPLOAD_DOCUMENT'   as PermissionString,
  DOC_DELETE:    'delete:document'   as PermissionString,
  // Hearings
  HEARING_VIEW:  'TRACK_CASE'        as PermissionString, // Advocates track cases to see hearings
  HEARING_SCHED: 'MANAGE_SCHEDULE'   as PermissionString,
  // Decisions
  DECISION_VIEW:   'DOWNLOAD_ORDER'  as PermissionString,
  DECISION_CREATE: 'SET_VERDICT'     as PermissionString,
  DECISION_APPROVE:'approve:decision'as PermissionString,
  // AI
  AI_VIEW:     'REVIEW_AI_ANALYSIS'  as PermissionString,
  AI_OVERRIDE: 'override:ai'         as PermissionString,
  // Admin
  USER_VIEW:   'MANAGE_USERS'        as PermissionString,
  USER_CREATE: 'MANAGE_USERS'        as PermissionString,
  USER_EDIT:   'MANAGE_USERS'        as PermissionString,
  USER_DELETE: 'MANAGE_USERS'        as PermissionString,
  AUDIT_VIEW:  'VIEW_AUDIT'          as PermissionString,
  // Reports
  REPORT_VIEW:   'VIEW_DASHBOARD'    as PermissionString,
  REPORT_EXPORT: 'export:report'     as PermissionString,
} as const

// ─── Role Labels ──────────────────────────────────────────────────────────────
export const ROLE_LABELS = {
  JUDGE:       'Judge',
  ADVOCATE:    'Advocate',
  CLERK:       'Court Clerk',
  ADMIN:       'Administrator',
  VIEWER:      'Viewer',
  RESEARCHER:  'Researcher',
  SUPER_ADMIN: 'Super Administrator',
  AUDITOR:     'Auditor',
  AI_OPERATOR: 'AI Operator',
} as const

// ─── Application Constants ────────────────────────────────────────────────────
export const APP_CONSTANTS = {
  APP_NAME:        'TrustCourt',
  APP_VERSION:     '2.0.0',
  APP_PHASE:       'F2',
  IDLE_TIMEOUT:    15 * 60 * 1000,   // 15 minutes
  /** @deprecated Use DynamicRefreshTimer (85% of expiresIn) instead of this fixed constant. */
  TOKEN_REFRESH:   5 * 60 * 1000,   // Legacy — kept for backwards compatibility
  REQUEST_TIMEOUT: 30_000,           // 30 seconds
  MAX_RETRIES:     3,
  STORAGE_PREFIX:  'tc_',
  QUERY_STALE:     5 * 60 * 1000,   // 5 minutes
} as const
