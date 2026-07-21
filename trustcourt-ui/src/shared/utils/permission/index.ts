// ─── Permission Utilities — Phase F3 ─────────────────────────────────────────
// Non-React permission helpers — for use in utils, services, and non-component code.
// Components should use usePermissions() hook instead.
import { store } from '@/store'
import { selectResolvedPermissions } from '@/store/slices/permissionSlice'
import { selectUserRole }            from '@/store/slices/authSlice'
import { ROLE_REGISTRY }             from '@/core/permissions'
import type { PermissionString }     from '@/types/permissions'

/** Check if current user has a permission (reads Redux state directly) */
export function can(permission: PermissionString): boolean {
  const perms = selectResolvedPermissions(store.getState())
  return perms.includes('*') || perms.includes(permission)
}

/** Check if current user has any of the given permissions */
export function canAny(permissions: PermissionString[]): boolean {
  return permissions.some(can)
}

/** Check if current user has all given permissions */
export function canAll(permissions: PermissionString[]): boolean {
  return permissions.every(can)
}

/** Check if current user has a specific role */
export function hasRole(role: string): boolean {
  return selectUserRole(store.getState()) === role
}

/** Check if current user is at least the given role rank */
export function isAtLeast(role: string): boolean {
  const current = selectUserRole(store.getState())
  if (!current) return false
  const ranks   = ROLE_REGISTRY
  const curRank = (ranks[current as keyof typeof ranks] as { rank?: number })?.rank ?? 0
  const minRank = (ranks[role as keyof typeof ranks] as { rank?: number })?.rank ?? 0
  return curRank >= minRank
}
