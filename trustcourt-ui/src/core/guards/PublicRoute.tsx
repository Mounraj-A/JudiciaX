// ─── PublicRoute ──────────────────────────────────────────────────────────────
// Phase F2 – Redirects authenticated users to their role home route.
// Use for: /login, /forgot-password, /reset-password
import React from 'react'
import { Navigate }       from 'react-router-dom'
import { useAppSelector } from '@/store'
import { selectIsAuthenticated, selectIsInitializing, selectUserRole } from '@/store/slices/authSlice'
import { getHomeRouteForRole } from '@/core/permissions'
import { AuthLoadingState }    from '@/features/auth/loading/AuthLoadingState'

interface PublicRouteProps {
  children: React.ReactNode
}

export function PublicRoute({ children }: PublicRouteProps) {
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const isInitializing  = useAppSelector(selectIsInitializing)
  const role            = useAppSelector(selectUserRole)

  if (isInitializing) return <AuthLoadingState />

  if (isAuthenticated && role) {
    return <Navigate to={getHomeRouteForRole(role)} replace />
  }

  return <>{children}</>
}
