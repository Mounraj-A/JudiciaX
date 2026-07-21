// ─── PermissionGuard ──────────────────────────────────────────────────────────
// Phase F2 – Renders children only if user has required permission(s).
// Reads from permissionSlice — not JWT.
import React from 'react'
import { Navigate }       from 'react-router-dom'
import { useAppSelector } from '@/store'
import { selectResolvedPermissions } from '@/store/slices/permissionSlice'
import { selectIsInitializing }      from '@/store/slices/authSlice'
import { hasPermission }             from '@/core/permissions'
import { ROUTES }                    from '@/constants/routes'
import { AuthLoadingState }          from '@/features/auth/loading/AuthLoadingState'
import type { PermissionString }     from '@/types/permissions'

interface PermissionGuardProps {
  require:    PermissionString | PermissionString[]
  children:   React.ReactNode
  fallback?:  React.ReactNode
}

export function PermissionGuard({ require, children, fallback }: PermissionGuardProps) {
  const isInitializing = useAppSelector(selectIsInitializing)
  const permissions    = useAppSelector(selectResolvedPermissions)

  if (isInitializing) return <AuthLoadingState />

  if (!hasPermission(permissions, require)) {
    return fallback ? <>{fallback}</> : <Navigate to={ROUTES.FORBIDDEN} replace />
  }

  return <>{children}</>
}
