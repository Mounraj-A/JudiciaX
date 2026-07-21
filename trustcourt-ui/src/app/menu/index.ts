// ─── Menu Architecture & Registry ─────────────────────────────────────────────
import type { NavigationItem } from '@/types/routes'
import type { UserRole } from '@/types/auth'
import { ROUTES } from '@/constants/routes'
import { PERMISSIONS } from '@/constants'
import { hasRole } from '@/core/permissions'

/**
 * Dynamic Menu Registry for TrustCourt navigation.
 * Each navigation entry is mapped to roles and permissions.
 */
export const MENU_REGISTRY: NavigationItem[] = [
  {
    id:          'judge-dashboard',
    label:       'Judge Dashboard',
    path:        ROUTES.JUDGE.DASHBOARD,
    roles:       ['JUDGE'],
  },
  {
    id:          'judge-assigned',
    label:       'Assigned Cases',
    path:        ROUTES.JUDGE.ASSIGNED,
    roles:       ['JUDGE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },
  {
    id:          'judge-cause-list',
    label:       'Cause List',
    path:        ROUTES.JUDGE.CAUSE_LIST,
    roles:       ['JUDGE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },
  {
    id:          'judge-reserved',
    label:       'Reserved Judgments',
    path:        ROUTES.JUDGE.RESERVED,
    roles:       ['JUDGE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },

  {
    id:          'advocate-dashboard',
    label:       'Advocate Dashboard',
    path:        ROUTES.ADVOCATE.DASHBOARD,
    roles:       ['ADVOCATE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },
  {
    id:          'advocate-cases',
    label:       'My Cases',
    path:        ROUTES.ADVOCATE.CASES,
    roles:       ['ADVOCATE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },
  {
    id:          'advocate-drafts',
    label:       'Draft Cases',
    path:        ROUTES.ADVOCATE.DRAFTS,
    roles:       ['ADVOCATE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },
  {
    id:          'advocate-new-case',
    label:       'Case Filing',
    path:        ROUTES.ADVOCATE.NEW_CASE,
    roles:       ['ADVOCATE'],
    permissions: [PERMISSIONS.CASE_CREATE],
  },
  {
    id:          'advocate-documents',
    label:       'Document Center',
    path:        ROUTES.ADVOCATE.DOCUMENTS,
    roles:       ['ADVOCATE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },
  {
    id:          'advocate-hearings',
    label:       'Hearing Timeline',
    path:        ROUTES.ADVOCATE.HEARINGS,
    roles:       ['ADVOCATE'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },

  {
    id:          'clerk-dashboard',
    label:       'Clerk Dashboard',
    path:        ROUTES.CLERK.DASHBOARD,
    roles:       ['CLERK'],
  },
  {
    id:          'clerk-submissions',
    label:       'New Submissions',
    path:        ROUTES.CLERK.SUBMISSIONS,
    roles:       ['CLERK'],
    permissions: [PERMISSIONS.CASE_VIEW],
  },
  {
    id:          'clerk-returned',
    label:       'Returned Cases',
    path:        ROUTES.CLERK.RETURNED,
    roles:       ['CLERK'],
  },
  {
    id:          'clerk-rejected',
    label:       'Rejected Cases',
    path:        ROUTES.CLERK.REJECTED,
    roles:       ['CLERK'],
  },
  {
    id:          'clerk-hearings',
    label:       'Hearing Scheduling',
    path:        ROUTES.CLERK.HEARINGS,
    roles:       ['CLERK'],
  },

  {
    id:          'admin-dashboard',
    label:       'Dashboard',
    path:        ROUTES.ADMIN.DASHBOARD,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-users',
    label:       'Users',
    path:        ROUTES.ADMIN.USERS,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
    permissions: [PERMISSIONS.USER_VIEW],
  },
  {
    id:          'admin-roles',
    label:       'Roles & Permissions',
    path:        ROUTES.ADMIN.ROLES,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-courts',
    label:       'Courts',
    path:        ROUTES.ADMIN.COURTS,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-judges',
    label:       'Judges',
    path:        ROUTES.ADMIN.JUDGES,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-advocates',
    label:       'Advocates',
    path:        ROUTES.ADMIN.ADVOCATES,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-clerks',
    label:       'Clerks',
    path:        ROUTES.ADMIN.CLERKS,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-cases',
    label:       'Case Administration',
    path:        ROUTES.ADMIN.CASES,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-documents',
    label:       'Document Administration',
    path:        ROUTES.ADMIN.DOCUMENTS,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-workflow',
    label:       'Workflow Monitoring',
    path:        ROUTES.ADMIN.WORKFLOW,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'ai-platform',
    label:       'AI & Explainability',
    path:        ROUTES.AI.ROOT,
    roles:       ['JUDGE', 'RESEARCHER', 'ADMIN'],
    permissions: [PERMISSIONS.AI_VIEW],
  },
  {
    id:          'admin-ai-services',
    label:       'AI Services',
    path:        ROUTES.ADMIN.AI_SERVICES,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'research-lab',
    label:       'Research Benchmarks',
    path:        ROUTES.RESEARCH.ROOT,
    roles:       ['RESEARCHER'],
    permissions: [PERMISSIONS.REPORT_VIEW],
  },
  {
    id:          'reports-audit',
    label:       'Audit & Reports',
    path:        ROUTES.REPORTS.ROOT,
    roles:       ['JUDGE', 'RESEARCHER'],
    permissions: [PERMISSIONS.REPORT_VIEW],
  },
  {
    id:          'admin-audit',
    label:       'Audit Logs',
    path:        ROUTES.ADMIN.AUDIT,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-reports',
    label:       'Reports',
    path:        ROUTES.ADMIN.REPORTS,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-health',
    label:       'System Health',
    path:        ROUTES.ADMIN.HEALTH,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
  {
    id:          'admin-settings',
    label:       'Settings',
    path:        ROUTES.ADMIN.SETTINGS,
    roles:       ['ADMIN', 'SUPER_ADMIN'],
  },
]

/** Returns filtered navigation items for a specific user role. */
export function getNavigationForRole(role: UserRole): NavigationItem[] {
  return MENU_REGISTRY.filter((item) => {
    if (!item.roles) return true
    return hasRole(role, item.roles)
  })
}
