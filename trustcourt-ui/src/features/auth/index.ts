// ─── Auth Feature Barrel ──────────────────────────────────────────────────────
// Phase F2 – Single import surface for all auth feature exports
//
// Usage:
//   import { useAuth, usePermissions, LoginPage, ProtectedRoute } from '@/features/auth'

// Hooks
export { useAuth }         from './hooks/useAuth'
export { useCurrentUser }  from './hooks/useCurrentUser'
export { usePermissions }  from './hooks/usePermissions'
export { useRole }         from './hooks/useRole'
export { useSession }      from './hooks/useSession'
export { useToken }        from './hooks/useToken'

// Contexts
export { AuthProvider, useAuthContext }             from './contexts/AuthContext'
export { IdentityProvider, useIdentityContext }     from './contexts/IdentityContext'
export { SessionProvider, useSessionContext }       from './contexts/SessionContext'
export { AuthorizationProvider, useAuthorizationContext } from './contexts/AuthorizationContext'
export { PermissionProvider, usePermissionContext } from './contexts/PermissionContext'

// Provider tree
export { AuthProviderTree } from './providers/AuthProviderTree'

// Pages
export { LoginPage }          from './pages/LoginPage'
export { ForgotPasswordPage } from './pages/ForgotPasswordPage'
export { ResetPasswordPage }  from './pages/ResetPasswordPage'
export { RegisterPage }       from './pages/RegisterPage'
export { SessionExpiredPage } from './pages/SessionExpiredPage'

// Components
export { IdleWarningDialog, IdleWarningDialogGuarded } from './components/IdleWarningDialog'
export { SessionStatusBar }                             from './components/SessionStatusBar'

// Loading states
export { AuthLoadingState }       from './loading/AuthLoadingState'
export { SilentRefreshIndicator } from './loading/SilentRefreshIndicator'

// Services (for advanced use cases only — prefer hooks and contexts)
export { authService }       from './services/authService'
export { tokenService }      from './services/tokenService'
export { sessionService }    from './services/sessionService'
export { permissionService } from './services/permissionService'
export { profileService }    from './services/profileService'
export { identityService }   from './services/identityService'
export { auditService }      from './services/auditService'

// Error types
export { AuthError, AuthErrorCode, isAuthError } from './errors/AuthError'
export { handleAuthError }                        from './errors/authErrorHandler'

// Queries
export { useCurrentUserQuery } from './queries/useCurrentUserQuery'
export { useProfileQuery }     from './queries/useProfileQuery'
export { usePermissionQuery }  from './queries/usePermissionQuery'
