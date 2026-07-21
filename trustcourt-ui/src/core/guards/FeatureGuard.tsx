// ─── FeatureGuard ─────────────────────────────────────────────────────────────
// Phase F2 – Conditionally renders children based on a feature flag.
//
// Primary use: REGISTRATION flag on LoginPage
//   <FeatureGuard feature="REGISTRATION">
//     <Link to="/register">Create Account</Link>
//   </FeatureGuard>
//
// When REGISTRATION=false (production), this renders nothing.
// No route mounting is needed — FeatureGuard purely controls UI visibility.
import React from 'react'
import { authFeatureFlags } from '@/core/permissions/featureFlags'
import type { AuthFeatureFlags } from '@/core/permissions/featureFlags'

interface FeatureGuardProps {
  feature:   keyof AuthFeatureFlags
  children:  React.ReactNode
  fallback?: React.ReactNode
}

export function FeatureGuard({ feature, children, fallback = null }: FeatureGuardProps) {
  const isEnabled = authFeatureFlags[feature] === true
  if (!isEnabled) return fallback ? <>{fallback}</> : null
  return <>{children}</>
}
