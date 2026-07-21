// ─── Authorization Context ────────────────────────────────────────────────────
// Phase F2 – Permission checking surface for the component tree
// Reads from Redux permissionSlice — owns no state.
// All check methods read from the single resolved permissions array.
import React, { createContext, useContext, useCallback } from 'react'
import { useAppSelector } from '@/store'
import {
  selectResolvedPermissions,
} from '@/store/slices/permissionSlice'
import { selectUserRole } from '@/store/slices/authSlice'
import {
  hasPermission, hasAnyPermission, hasRole, canAccessRoute, isAtLeastRole,
} from '@/core/permissions'
import { authFeatureFlags }   from '@/core/permissions/featureFlags'
import type { PermissionString } from '@/types/permissions'
import type { UserRole }        from '@/types/auth'
import type { AuthFeatureFlags } from '@/core/permissions/featureFlags'

export interface AuthorizationContextValue {
  can:           (permission: PermissionString) => boolean
  canAny:        (permissions: PermissionString[]) => boolean
  hasRole:       (roles: UserRole | UserRole[]) => boolean
  canAccess:     (route: string) => boolean
  isAtLeast:     (minRole: UserRole) => boolean
  checkFeature:  (feature: keyof AuthFeatureFlags) => boolean
}

const AuthorizationContext = createContext<AuthorizationContextValue | null>(null)

export function AuthorizationProvider({ children }: { children: React.ReactNode }) {
  const permissions    = useAppSelector(selectResolvedPermissions)
  const role           = useAppSelector(selectUserRole)

  const can          = useCallback((p: PermissionString)         => hasPermission(permissions, p),          [permissions])
  const canAny_      = useCallback((ps: PermissionString[])      => hasAnyPermission(permissions, ps),      [permissions])
  const hasRole_     = useCallback((r: UserRole | UserRole[])    => {
    if (!role) return false
    const list = Array.isArray(r) ? r : [r]
    return hasRole(role, list)
  }, [role])
  const canAccess_   = useCallback((route: string) => {
    if (!role) return false
    return canAccessRoute(role, route)
  }, [role])
  const isAtLeast_   = useCallback((minRole: UserRole) => {
    if (!role) return false
    return isAtLeastRole(role, minRole)
  }, [role])
  const checkFeature = useCallback((flag: keyof AuthFeatureFlags) => authFeatureFlags[flag] === true, [])

  return (
    <AuthorizationContext.Provider value={{
      can, canAny: canAny_, hasRole: hasRole_, canAccess: canAccess_,
      isAtLeast: isAtLeast_, checkFeature,
    }}>
      {children}
    </AuthorizationContext.Provider>
  )
}

export function useAuthorizationContext(): AuthorizationContextValue {
  const ctx = useContext(AuthorizationContext)
  if (!ctx) throw new Error('useAuthorizationContext must be used within <AuthorizationProvider>')
  return ctx
}
