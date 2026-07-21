// ─── Permission System — Phase F2 ─────────────────────────────────────────────
// Extended with AUDITOR, AI_OPERATOR roles.
// Adding a future role: add it to ROLE_REGISTRY only — no service/guard changes needed.
import type { UserRole } from '@/types/auth'
import type { PermissionString, RoleDefinition } from '@/types/permissions'
import { PERMISSIONS, ROLE_LABELS } from '@/constants'
import { ROUTES } from '@/constants/routes'

// ─── Role Registry ────────────────────────────────────────────────────────────
export const ROLE_REGISTRY: Record<UserRole, RoleDefinition> = {
  JUDGE: {
    role:        'JUDGE',
    label:       ROLE_LABELS.JUDGE,
    description: 'Judicial officer with case review and decision authority',
    homeRoute:   ROUTES.JUDGE.ROOT,
    routes:      [ROUTES.JUDGE.ROOT, ROUTES.PROFILE, ROUTES.SETTINGS],
    permissions: [
      PERMISSIONS.CASE_VIEW,     PERMISSIONS.CASE_ASSIGN,
      PERMISSIONS.DOC_VIEW,
      PERMISSIONS.HEARING_VIEW,  PERMISSIONS.HEARING_SCHED,
      PERMISSIONS.DECISION_VIEW, PERMISSIONS.DECISION_CREATE, PERMISSIONS.DECISION_APPROVE,
      PERMISSIONS.AI_VIEW,       PERMISSIONS.AI_OVERRIDE,
      PERMISSIONS.REPORT_VIEW,   PERMISSIONS.REPORT_EXPORT,
      PERMISSIONS.AUDIT_VIEW,
    ],
  },

  ADVOCATE: {
    role:        'ADVOCATE',
    label:       ROLE_LABELS.ADVOCATE,
    description: 'Legal practitioner representing clients before the court',
    homeRoute:   ROUTES.ADVOCATE.DASHBOARD,
    routes:      [ROUTES.ADVOCATE.ROOT, ROUTES.PROFILE],
    permissions: [
      PERMISSIONS.CASE_VIEW,   PERMISSIONS.CASE_CREATE,
      PERMISSIONS.DOC_VIEW,    PERMISSIONS.DOC_CREATE,
      PERMISSIONS.HEARING_VIEW,
      PERMISSIONS.DECISION_VIEW,
    ],
  },

  CLERK: {
    role:        'CLERK',
    label:       ROLE_LABELS.CLERK,
    description: 'Court administrative officer handling case management',
    homeRoute:   ROUTES.CLERK.DASHBOARD,
    routes:      [ROUTES.CLERK.ROOT, ROUTES.PROFILE],
    permissions: [
      PERMISSIONS.CASE_VIEW,   PERMISSIONS.CASE_CREATE,   PERMISSIONS.CASE_EDIT,
      PERMISSIONS.DOC_VIEW,    PERMISSIONS.DOC_CREATE,    PERMISSIONS.DOC_DELETE,
      PERMISSIONS.HEARING_VIEW,PERMISSIONS.HEARING_SCHED,
      PERMISSIONS.DECISION_VIEW,
    ],
  },

  ADMIN: {
    role:        'ADMIN',
    label:       ROLE_LABELS.ADMIN,
    description: 'Platform administrator with user and system management access',
    homeRoute:   ROUTES.ADMIN.ROOT,
    routes:      [ROUTES.ADMIN.ROOT, ROUTES.PROFILE, ROUTES.SETTINGS],
    permissions: [
      PERMISSIONS.CASE_VIEW,   PERMISSIONS.CASE_EDIT,
      PERMISSIONS.USER_VIEW,   PERMISSIONS.USER_CREATE, PERMISSIONS.USER_EDIT, PERMISSIONS.USER_DELETE,
      PERMISSIONS.AUDIT_VIEW,
      PERMISSIONS.REPORT_VIEW, PERMISSIONS.REPORT_EXPORT,
    ],
  },

  SUPER_ADMIN: {
    role:        'SUPER_ADMIN',
    label:       ROLE_LABELS.SUPER_ADMIN,
    description: 'Full platform access with all permissions',
    homeRoute:   ROUTES.ADMIN.ROOT,
    routes:      ['*'],
    permissions: Object.values(PERMISSIONS),
  },

  AUDITOR: {
    role:        'AUDITOR',
    label:       'Auditor',
    description: 'Read-only audit and compliance access across all case activity',
    homeRoute:   ROUTES.ADMIN.AUDIT,
    routes:      [ROUTES.ADMIN.AUDIT, ROUTES.REPORTS.ROOT, ROUTES.PROFILE],
    permissions: [
      PERMISSIONS.AUDIT_VIEW,
      PERMISSIONS.REPORT_VIEW, PERMISSIONS.REPORT_EXPORT,
      PERMISSIONS.CASE_VIEW,
    ],
  },

  AI_OPERATOR: {
    role:        'AI_OPERATOR',
    label:       'AI Operator',
    description: 'AI pipeline management and governance oversight',
    homeRoute:   ROUTES.AI.DASHBOARD,
    routes:      [ROUTES.AI.ROOT, ROUTES.RESEARCH.ROOT, ROUTES.REPORTS.ROOT, ROUTES.PROFILE],
    permissions: [
      PERMISSIONS.AI_VIEW,     PERMISSIONS.AI_OVERRIDE,
      PERMISSIONS.REPORT_VIEW, PERMISSIONS.REPORT_EXPORT,
      PERMISSIONS.CASE_VIEW,
    ],
  },

  RESEARCHER: {
    role:        'RESEARCHER',
    label:       ROLE_LABELS.RESEARCHER,
    description: 'Research and analytics access for AI performance evaluation',
    homeRoute:   ROUTES.RESEARCH.DASHBOARD,
    routes:      [ROUTES.RESEARCH.ROOT, ROUTES.AI.ROOT, ROUTES.REPORTS.ROOT],
    permissions: [
      PERMISSIONS.CASE_VIEW,
      PERMISSIONS.AI_VIEW,
      PERMISSIONS.REPORT_VIEW, PERMISSIONS.REPORT_EXPORT,
      PERMISSIONS.AUDIT_VIEW,
    ],
  },

  VIEWER: {
    role:        'VIEWER',
    label:       ROLE_LABELS.VIEWER,
    description: 'Read-only access to case and report data',
    homeRoute:   ROUTES.DASHBOARD,
    routes:      [ROUTES.DASHBOARD, ROUTES.PROFILE],
    permissions: [PERMISSIONS.CASE_VIEW, PERMISSIONS.REPORT_VIEW],
  },
}

// ─── Permission Checkers ──────────────────────────────────────────────────────
export function hasPermission(
  userPermissions: string[],
  required: PermissionString | PermissionString[]
): boolean {
  const required_ = Array.isArray(required) ? required : [required]
  return required_.every((p) => userPermissions.includes(p))
}

export function hasAnyPermission(
  userPermissions: string[],
  required: PermissionString[]
): boolean {
  return required.some((p) => userPermissions.includes(p))
}

export function hasRole(userRole: UserRole, allowed: UserRole[]): boolean {
  return allowed.includes(userRole)
}

export function getRoleDefinition(role: UserRole): RoleDefinition {
  return ROLE_REGISTRY[role]
}

export function getHomeRouteForRole(role: UserRole): string {
  return ROLE_REGISTRY[role]?.homeRoute ?? ROUTES.DASHBOARD
}

export function canAccessRoute(userRole: UserRole, path: string): boolean {
  const def = ROLE_REGISTRY[userRole]
  if (!def) return false
  if (def.routes.includes('*')) return true
  return def.routes.some((r) => path.startsWith(r))
}

// ─── Role Hierarchy ───────────────────────────────────────────────────────────
/** Numeric rank for isAtLeast() comparisons. Higher = more privileged. */
export const ROLE_RANK: Record<UserRole, number> = {
  VIEWER:      0,
  ADVOCATE:    1,
  CLERK:       1,
  RESEARCHER:  2,
  AUDITOR:     2,
  AI_OPERATOR: 2,
  JUDGE:       3,
  ADMIN:       4,
  SUPER_ADMIN: 5,
}

export function isAtLeastRole(userRole: UserRole, minRole: UserRole): boolean {
  return ROLE_RANK[userRole] >= ROLE_RANK[minRole]
}
