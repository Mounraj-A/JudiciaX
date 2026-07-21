// ─── Wrapper Components — Phase F3 ───────────────────────────────────────────
import React from 'react'
import { usePermissions } from '@/features/auth/hooks/usePermissions'
import { authFeatureFlags } from '@/core/permissions/featureFlags'
import type { PermissionString } from '@/types/permissions'

// ─── PermissionWrapper ───────────────────────────────────────────────────────
interface PermissionWrapperProps {
  requires:   PermissionString | PermissionString[]
  mode?:      'any' | 'all'
  fallback?:  React.ReactNode
  children:   React.ReactNode
}
export function PermissionWrapper({ requires, mode = 'any', fallback = null, children }: PermissionWrapperProps) {
  const { can } = usePermissions()
  const perms   = Array.isArray(requires) ? requires : [requires]
  const allowed = mode === 'any' ? perms.some(can) : perms.every(can)
  return <>{allowed ? children : fallback}</>
}

// ─── FeatureWrapper ───────────────────────────────────────────────────────────
interface FeatureWrapperProps {
  feature:   keyof typeof authFeatureFlags
  fallback?: React.ReactNode
  children:  React.ReactNode
}
export function FeatureWrapper({ feature, fallback = null, children }: FeatureWrapperProps) {
  const enabled = authFeatureFlags[feature]
  return <>{enabled ? children : fallback}</>
}

// ─── ScrollableArea ───────────────────────────────────────────────────────────
interface ScrollableAreaProps {
  children:   React.ReactNode
  maxHeight?: string
  direction?: 'vertical' | 'horizontal' | 'both'
  className?: string
}
export function ScrollableArea({ children, maxHeight = '400px', direction = 'vertical', className = '' }: ScrollableAreaProps) {
  return (
    <div className={className} style={{
      maxHeight: direction !== 'horizontal' ? maxHeight : undefined,
      maxWidth:  direction === 'horizontal' ? '100%' : undefined,
      overflowY: direction !== 'horizontal' ? 'auto' : 'hidden',
      overflowX: direction !== 'vertical'   ? 'auto' : 'hidden',
      scrollbarWidth: 'thin',
      scrollbarColor: '#D1D5DB transparent',
    }}>
      {children}
    </div>
  )
}
