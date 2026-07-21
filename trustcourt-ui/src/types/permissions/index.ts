// ─── Permission & Role Types ──────────────────────────────────────────────────
import type { UserRole } from '../auth'

export type PermissionAction =
  | 'view' | 'create' | 'edit' | 'delete'
  | 'approve' | 'reject' | 'submit' | 'export'
  | 'assign' | 'schedule' | 'override'

export type PermissionSubject =
  | 'case' | 'document' | 'hearing' | 'decision'
  | 'user' | 'audit' | 'report' | 'ai'
  | 'governance' | 'settings' | 'all'

export interface Permission {
  action:  string
  subject: string
}

// Backend uses literal strings like 'VIEW_CASE_DETAILS', 'CREATE_CASE', etc.
export type PermissionString = string

export interface RoleDefinition {
  role:        UserRole
  label:       string
  description: string
  permissions: PermissionString[]
  routes:      string[]
  homeRoute:   string
}

export interface AbilityRule {
  action:      PermissionAction | PermissionAction[]
  subject:     PermissionSubject | PermissionSubject[]
  conditions?: Record<string, unknown>
  inverted?:   boolean
}
