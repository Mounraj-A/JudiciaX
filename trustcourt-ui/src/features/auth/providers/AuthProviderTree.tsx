// ─── Auth Provider Tree ───────────────────────────────────────────────────────
// Phase F2 – Composes all 5 auth contexts into a single wrapper
//
// Usage (in AppProviders.tsx — one place only):
//   <AuthProviderTree>{children}</AuthProviderTree>
//
// Provider nesting order:
//   SessionProvider  (must wrap Auth — watches sessionExpired → triggers logout)
//   AuthProvider     (core auth state + actions)
//   IdentityProvider (extended profile)
//   AuthorizationProvider (permission checking)
//   PermissionProvider (component registry)
import React from 'react'
import { AuthProvider }          from '../contexts/AuthContext'
import { IdentityProvider }      from '../contexts/IdentityContext'
import { SessionProvider }       from '../contexts/SessionContext'
import { AuthorizationProvider } from '../contexts/AuthorizationContext'
import { PermissionProvider }    from '../contexts/PermissionContext'

interface AuthProviderTreeProps {
  children: React.ReactNode
}

/**
 * AuthProviderTree — single composition point for all Phase F2 auth contexts.
 *
 * Place this once in AppProviders.tsx, inside QueryClientProvider + ReduxProvider.
 * All auth hooks (useAuth, usePermissions, useSession, etc.) must be used within this tree.
 */
export function AuthProviderTree({ children }: AuthProviderTreeProps) {
  return (
    <AuthProvider>
      <SessionProvider>
        <IdentityProvider>
          <AuthorizationProvider>
            <PermissionProvider>
              {children}
            </PermissionProvider>
          </AuthorizationProvider>
        </IdentityProvider>
      </SessionProvider>
    </AuthProvider>
  )
}
