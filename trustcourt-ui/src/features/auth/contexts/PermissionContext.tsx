// ─── Permission Context ───────────────────────────────────────────────────────
// Phase F2 – Component-level permission registry exposure
// Reads from permissionSlice — never owns its own copy of permissions.
import React, { createContext, useContext, useCallback } from 'react'
import { useAppSelector } from '@/store'
import { selectResolvedPermissions, selectRoleDefinition } from '@/store/slices/permissionSlice'
import { permissionRegistry } from '@/core/permissions/permissionRegistry'
import type { PermissionString, RoleDefinition } from '@/types/permissions'

export interface PermissionContextValue {
  resolvedPermissions: PermissionString[]
  roleDefinition:      RoleDefinition | null
  isAllowed:           (componentId: string) => boolean
}

const PermissionContext = createContext<PermissionContextValue | null>(null)

export function PermissionProvider({ children }: { children: React.ReactNode }) {
  const resolvedPermissions = useAppSelector(selectResolvedPermissions)
  const roleDefinition      = useAppSelector(selectRoleDefinition)

  const isAllowed = useCallback(
    (componentId: string) => permissionRegistry.check(componentId, resolvedPermissions),
    [resolvedPermissions]
  )

  return (
    <PermissionContext.Provider value={{ resolvedPermissions, roleDefinition, isAllowed }}>
      {children}
    </PermissionContext.Provider>
  )
}

export function usePermissionContext(): PermissionContextValue {
  const ctx = useContext(PermissionContext)
  if (!ctx) throw new Error('usePermissionContext must be used within <PermissionProvider>')
  return ctx
}
