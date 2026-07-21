// ─── RoleGuard ────────────────────────────────────────────────────────────────
// Phase F2 – Restricts route to specific roles. Redirects to /403 otherwise.
// Reads from permissionSlice (resolved role definition) — not JWT.
import React from 'react'
import { Navigate }       from 'react-router-dom'
import { useAppSelector } from '@/store'
import { selectIsInitializing, selectUserRole } from '@/store/slices/authSlice'
import { hasRole }        from '@/core/permissions'
import { ROUTES }         from '@/constants/routes'
import { AuthLoadingState } from '@/features/auth/loading/AuthLoadingState'
import type { UserRole }  from '@/types/auth'

interface RoleGuardProps {
  allowedRoles: UserRole[]
  children:     React.ReactNode
  fallback?:    React.ReactNode
}

export function RoleGuard({ allowedRoles, children, fallback }: RoleGuardProps) {
  const isInitializing = useAppSelector(selectIsInitializing)
  const role           = useAppSelector(selectUserRole)

  if (isInitializing) return <AuthLoadingState />

  if (!role || !hasRole(role, allowedRoles)) {
    return fallback ? <>{fallback}</> : <Navigate to={ROUTES.FORBIDDEN} replace />
  }

  return <>{children}</>
}
