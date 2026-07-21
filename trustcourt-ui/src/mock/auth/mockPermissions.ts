// ─── Mock Permissions — Dev/Test Infrastructure ───────────────────────────────
import type { PermissionString } from '@/types/permissions'
import type { UserRole }         from '@/types/auth'
import { ROLE_REGISTRY }         from '@/core/permissions'

/** Get resolved permissions for a role — matches production ROLE_REGISTRY exactly. */
export function getMockPermissions(role: UserRole): PermissionString[] {
  return ROLE_REGISTRY[role]?.permissions ?? []
}

/** Pre-resolved permission sets for common test roles */
export const mockPermissions: Record<string, PermissionString[]> = {
  JUDGE:      getMockPermissions('JUDGE'),
  ADVOCATE:   getMockPermissions('ADVOCATE'),
  CLERK:      getMockPermissions('CLERK'),
  ADMIN:      getMockPermissions('ADMIN'),
  SUPER_ADMIN:getMockPermissions('SUPER_ADMIN'),
  AUDITOR:    getMockPermissions('AUDITOR'),
  AI_OPERATOR:getMockPermissions('AI_OPERATOR'),
  RESEARCHER: getMockPermissions('RESEARCHER'),
  VIEWER:     getMockPermissions('VIEWER'),
}
