// ─── Mock Users — Dev/Test Infrastructure ─────────────────────────────────────
// Phase F2 — Used in component tests and future Storybook stories
import type { AuthUser } from '@/types/auth'

export const mockUsers: Record<string, AuthUser> = {
  JUDGE: {
    id: 'mock-judge-001', name: 'Hon. Justice A. Kumar', email: 'judge@court.gov.in',
    role: 'JUDGE', permissions: ['view:case','approve:decision','view:ai','override:ai','view:audit'],
    isActive: true, courtId: 'court-hc-tn', courtName: 'Madras High Court',
    accountStatus: 'ACTIVE', emailVerified: true,
  },
  ADVOCATE: {
    id: 'mock-adv-001', name: 'Adv. B. Sharma', email: 'advocate@lawfirm.com',
    role: 'ADVOCATE', permissions: ['view:case','create:case','view:document','create:document'],
    isActive: true, accountStatus: 'ACTIVE', emailVerified: true,
  },
  CLERK: {
    id: 'mock-clerk-001', name: 'Clerk P. Nair', email: 'clerk@court.gov.in',
    role: 'CLERK', permissions: ['view:case','create:case','edit:case','view:document','create:document','delete:document','schedule:hearing'],
    isActive: true, courtId: 'court-hc-tn', accountStatus: 'ACTIVE', emailVerified: true,
  },
  ADMIN: {
    id: 'mock-admin-001', name: 'Admin S. Patel', email: 'admin@trustcourt.gov',
    role: 'ADMIN', permissions: ['view:user','create:user','edit:user','delete:user','view:audit','view:report'],
    isActive: true, accountStatus: 'ACTIVE', emailVerified: true,
  },
  SUPER_ADMIN: {
    id: 'mock-super-001', name: 'Super Admin R. Singh', email: 'superadmin@trustcourt.gov',
    role: 'SUPER_ADMIN', permissions: ['*'],
    isActive: true, accountStatus: 'ACTIVE', emailVerified: true,
  },
  AUDITOR: {
    id: 'mock-auditor-001', name: 'Auditor T. Menon', email: 'auditor@court.gov.in',
    role: 'AUDITOR', permissions: ['view:audit','view:report','view:case'],
    isActive: true, accountStatus: 'ACTIVE', emailVerified: true,
  },
  AI_OPERATOR: {
    id: 'mock-ai-001', name: 'AI Ops D. Rao', email: 'aiops@trustcourt.ai',
    role: 'AI_OPERATOR', permissions: ['view:ai','override:ai','view:governance'],
    isActive: true, accountStatus: 'ACTIVE', emailVerified: true,
  },
  RESEARCHER: {
    id: 'mock-res-001', name: 'Dr. M. Iyer', email: 'researcher@nlu.ac.in',
    role: 'RESEARCHER', permissions: ['view:case','view:ai','view:report'],
    isActive: true, accountStatus: 'ACTIVE', emailVerified: true,
  },
  VIEWER: {
    id: 'mock-viewer-001', name: 'Observer G. Das', email: 'viewer@court.gov.in',
    role: 'VIEWER', permissions: ['view:case'],
    isActive: true, accountStatus: 'ACTIVE', emailVerified: true,
  },
}
