// ─── GuestRoute ───────────────────────────────────────────────────────────────
// Phase F2 – Strictly unauthenticated access only.
// More restrictive than PublicRoute — for pages that must never show to logged-in users.
import React from 'react'
import { Navigate }       from 'react-router-dom'
import { useAppSelector } from '@/store'
import { selectIsAuthenticated, selectIsInitializing } from '@/store/slices/authSlice'
import { AuthLoadingState } from '@/features/auth/loading/AuthLoadingState'
import { ROUTES }           from '@/constants/routes'

interface GuestRouteProps {
  children: React.ReactNode
}

export function GuestRoute({ children }: GuestRouteProps) {
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const isInitializing  = useAppSelector(selectIsInitializing)

  if (isInitializing) return <AuthLoadingState />

  if (isAuthenticated) {
    return <Navigate to={ROUTES.DASHBOARD} replace />
  }

  return <>{children}</>
}
