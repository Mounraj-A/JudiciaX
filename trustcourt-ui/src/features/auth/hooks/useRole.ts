// ─── useRole Hook ─────────────────────────────────────────────────────────────
// Phase F2 – Role information and hierarchy checks
import { useAppSelector }       from '@/store'
import { selectUserRole }       from '@/store/slices/authSlice'
import { selectRoleDefinition } from '@/store/slices/permissionSlice'
import { ROLE_LABELS }          from '@/constants'
import { isAtLeastRole, ROLE_RANK } from '@/core/permissions'
import type { UserRole }        from '@/types/auth'

export function useRole() {
  const role           = useAppSelector(selectUserRole)
  const roleDefinition = useAppSelector(selectRoleDefinition)

  const isRole = (roles: UserRole | UserRole[]): boolean => {
    if (!role) return false
    const list = Array.isArray(roles) ? roles : [roles]
    return list.includes(role)
  }

  const isAtLeast = (minRole: UserRole): boolean => {
    if (!role) return false
    return isAtLeastRole(role, minRole)
  }

  const roleLabel = role
    ? (ROLE_LABELS as Record<string, string>)[role] ?? role
    : ''

  return {
    role,
    roleLabel,
    roleDefinition,
    homeRoute:   roleDefinition?.homeRoute ?? '/dashboard',
    rank:        role ? ROLE_RANK[role] : 0,
    isRole,
    isAtLeast,
  }
}
