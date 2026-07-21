// ─── Application Core Infrastructure Hooks ────────────────────────────────────
import { useEffect, useCallback } from 'react'
import { useAppSelector } from '@/store'
import {
  selectCurrentUser,
  selectIsAuthenticated,
  selectUserRole,
  selectUserPermissions,
} from '@/store/slices/authSlice'
import { hasPermission, hasRole } from '@/core/permissions'
import type { UserRole } from '@/types/auth'
import type { PermissionString } from '@/types/permissions'
import { eventBus } from '@/core/events'
import type { AppEventType, EventHandler } from '@/types/events'

// ─── useAuth Hook ─────────────────────────────────────────────────────────────
export function useAuth() {
  const user            = useAppSelector(selectCurrentUser)
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const role            = useAppSelector(selectUserRole)

  return {
    user,
    isAuthenticated,
    role,
    isJudge:     role === 'JUDGE',
    isAdvocate:  role === 'ADVOCATE',
    isClerk:     role === 'CLERK',
    isAdmin:     role === 'ADMIN' || role === 'SUPER_ADMIN',
  }
}

// ─── usePermission Hook ───────────────────────────────────────────────────────
export function usePermission() {
  const permissions = useAppSelector(selectUserPermissions)
  const role        = useAppSelector(selectUserRole)

  const can = useCallback(
    (actionOrPermission: PermissionString | PermissionString[]) => {
      return hasPermission(permissions, actionOrPermission)
    },
    [permissions]
  )

  const isRole = useCallback(
    (roles: UserRole | UserRole[]) => {
      if (!role) return false
      const list = Array.isArray(roles) ? roles : [roles]
      return hasRole(role, list)
    },
    [role]
  )

  return { can, isRole, permissions }
}

// ─── useEventBus Hook ─────────────────────────────────────────────────────────
export function useEventBus<T = unknown>(type: AppEventType, handler: EventHandler<T>) {
  useEffect(() => {
    return eventBus.on(type, handler)
  }, [type, handler])
}
