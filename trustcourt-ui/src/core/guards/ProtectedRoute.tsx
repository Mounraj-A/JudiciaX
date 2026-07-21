// ─── ProtectedRoute ───────────────────────────────────────────────────────────
// Phase F2 – Requires authentication. Redirects guests to /login.
import React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAppSelector }        from '@/store'
import { selectIsAuthenticated, selectIsInitializing } from '@/store/slices/authSlice'
import { ROUTES }                from '@/constants/routes'
import { AuthLoadingState }      from '@/features/auth/loading/AuthLoadingState'

interface ProtectedRouteProps {
  children: React.ReactNode
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const isInitializing  = useAppSelector(selectIsInitializing)
  const location        = useLocation()

  if (isInitializing) return <AuthLoadingState />

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} state={{ from: location }} replace />
  }

  return <>{children}</>
}
