// ─── Route Guards Infrastructure ─────────────────────────────────────────────
import React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAppSelector } from '@/store'
import { selectIsAuthenticated, selectUserRole, selectUserPermissions, selectIsInitializing } from '@/store/slices/authSlice'
import { hasPermission, hasRole, getHomeRouteForRole } from '@/core/permissions'
import type { UserRole } from '@/types/auth'
import type { PermissionString } from '@/types/permissions'
import { ROUTES } from '@/constants/routes'
import { LoadingSpinner } from '@/shared/design-system/components/feedback'

// ─── AuthGuard ────────────────────────────────────────────────────────────────
/** Protects routes requiring authentication. Redirects guests to login. */
export function AuthGuard({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const isInitializing  = useAppSelector(selectIsInitializing)
  const location        = useLocation()

  if (isInitializing) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-[#F8F9FA]">
        <LoadingSpinner size="lg" label="Initializing TrustCourt session..." />
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} state={{ from: location }} replace />
  }

  return <>{children}</>
}

// ─── GuestGuard ───────────────────────────────────────────────────────────────
/** Protects public/guest routes (login, forgot password). Redirects authenticated users to their workspace. */
export function GuestGuard({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const isInitializing  = useAppSelector(selectIsInitializing)
  const role            = useAppSelector(selectUserRole)

  if (isInitializing) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-[#F8F9FA]">
        <LoadingSpinner size="lg" label="Checking authentication..." />
      </div>
    )
  }

  if (isAuthenticated && role) {
    // Redirect authenticated users to their role-specific workspace root
    return <Navigate to={getHomeRouteForRole(role)} replace />
  }

  return <>{children}</>
}

// ─── RoleGuard ────────────────────────────────────────────────────────────────
/** Ensures user has at least one of the allowed roles. Redirects to 403 otherwise. */
interface RoleGuardProps {
  allowedRoles: UserRole[]
  children: React.ReactNode
}

export function RoleGuard({ allowedRoles, children }: RoleGuardProps) {
  const role            = useAppSelector(selectUserRole)
  const isInitializing  = useAppSelector(selectIsInitializing)

  if (isInitializing) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-[#F8F9FA]">
        <LoadingSpinner size="lg" label="Verifying role access..." />
      </div>
    )
  }

  if (!role || !hasRole(role, allowedRoles)) {
    return <Navigate to={ROUTES.FORBIDDEN} replace />
  }

  return <>{children}</>
}

// ─── PermissionGuard ──────────────────────────────────────────────────────────
/** Ensures user has required permissions. Redirects to 403 otherwise. */
interface PermissionGuardProps {
  requiredPermissions: PermissionString | PermissionString[]
  children: React.ReactNode
}

export function PermissionGuard({ requiredPermissions, children }: PermissionGuardProps) {
  const userPermissions = useAppSelector(selectUserPermissions)
  const isInitializing  = useAppSelector(selectIsInitializing)

  if (isInitializing) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-[#F8F9FA]">
        <LoadingSpinner size="lg" label="Checking permissions..." />
      </div>
    )
  }

  if (!hasPermission(userPermissions, requiredPermissions)) {
    return <Navigate to={ROUTES.FORBIDDEN} replace />
  }

  return <>{children}</>
}
