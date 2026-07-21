// ─── Auth Context ─────────────────────────────────────────────────────────────
// Phase F2 – Read-only bridge from Redux authSlice to component tree
//
// ⚠️  ARCHITECTURE RULE: This context owns NO state.
//     It reads from Redux selectors and exposes values + action callbacks.
//     The single source of truth is Redux authSlice.
import React, { createContext, useContext, useCallback } from 'react'
import { useAppSelector, useAppDispatch }  from '@/store'
import {
  selectCurrentUser, selectIsAuthenticated, selectIsInitializing,
  selectIsRefreshing, selectAuthError,
} from '@/store/slices/authSlice'
import { authService }      from '@/features/auth/services/authService'
import type { LoginCredentials, RegisterRequest, ForgotPasswordRequest, ResetPasswordRequest } from '@/types/auth'
import type { AuthUser }    from '@/types/auth'

// ─── Context Shape ────────────────────────────────────────────────────────────
export interface AuthContextValue {
  user:            AuthUser | null
  isAuthenticated: boolean
  isInitializing:  boolean
  isRefreshing:    boolean
  error:           string | null
  login:           (credentials: LoginCredentials) => Promise<void>
  logout:          () => Promise<void>
  refresh:         () => Promise<void>
  register:        (request: RegisterRequest) => Promise<void>
  forgotPassword:  (request: ForgotPasswordRequest) => Promise<void>
  resetPassword:   (request: ResetPasswordRequest) => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

// ─── Provider ─────────────────────────────────────────────────────────────────
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const dispatch        = useAppDispatch()
  const user            = useAppSelector(selectCurrentUser)
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const isInitializing  = useAppSelector(selectIsInitializing)
  const isRefreshing    = useAppSelector(selectIsRefreshing)
  const error           = useAppSelector(selectAuthError)

  // Ensure authService has dispatch
  React.useEffect(() => { authService.init(dispatch) }, [dispatch])

  const login          = useCallback(async (c: LoginCredentials) => { await authService.login(c)           }, [])
  const logout         = useCallback(async ()                    => { await authService.logout()            }, [])
  const refresh        = useCallback(async ()                    => { await authService.refresh()           }, [])
  const register       = useCallback(async (r: RegisterRequest)        => { await authService.register(r)  }, [])
  const forgotPassword = useCallback(async (r: ForgotPasswordRequest)  => { await authService.forgotPassword(r) }, [])
  const resetPassword  = useCallback(async (r: ResetPasswordRequest)   => { await authService.resetPassword(r) }, [])

  const value: AuthContextValue = {
    user, isAuthenticated, isInitializing, isRefreshing, error,
    login, logout, refresh, register, forgotPassword, resetPassword,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// ─── Hook ─────────────────────────────────────────────────────────────────────
export function useAuthContext(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuthContext must be used within <AuthProvider>')
  return ctx
}
