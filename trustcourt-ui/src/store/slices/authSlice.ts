// ─── Redux Auth Slice — Phase F2 ──────────────────────────────────────────────
import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { AuthState, AuthUser, AuthTokens } from '@/types/auth'

const initialState: AuthState = {
  user:            null,
  tokens:          null,
  isAuthenticated: false,
  isInitializing:  true,
  isRefreshing:    false,
  refreshError:    null,
  rememberMe:      false,
  loginTime:       null,
  error:           null,
}

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setInitializing(state, action: PayloadAction<boolean>) {
      state.isInitializing = action.payload
    },
    setCredentials(state, action: PayloadAction<{ user: AuthUser; tokens: AuthTokens }>) {
      state.user            = action.payload.user
      state.tokens          = action.payload.tokens
      state.isAuthenticated = true
      state.error           = null
      state.isInitializing  = false
      state.loginTime       = action.payload.tokens.loginTime ?? new Date().toISOString()
    },
    updateUser(state, action: PayloadAction<Partial<AuthUser>>) {
      if (state.user) Object.assign(state.user, action.payload)
    },
    updateTokens(state, action: PayloadAction<AuthTokens>) {
      state.tokens      = action.payload
      state.isRefreshing = false
      state.refreshError = null
    },
    clearCredentials(state) {
      state.user            = null
      state.tokens          = null
      state.isAuthenticated = false
      state.error           = null
      state.isInitializing  = false
      state.isRefreshing    = false
      state.refreshError    = null
      state.loginTime       = null
    },
    setError(state, action: PayloadAction<string>) {
      state.error          = action.payload
      state.isInitializing = false
    },
    setRefreshing(state, action: PayloadAction<boolean>) {
      state.isRefreshing = action.payload
      if (action.payload) state.refreshError = null
    },
    setRefreshError(state, action: PayloadAction<string>) {
      state.refreshError = action.payload
      state.isRefreshing = false
    },
    setRememberMe(state, action: PayloadAction<boolean>) {
      state.rememberMe = action.payload
    },
    setLoginTime(state, action: PayloadAction<string>) {
      state.loginTime = action.payload
    },
  },
})

export const {
  setInitializing,
  setCredentials,
  updateUser,
  updateTokens,
  clearCredentials,
  setError,
  setRefreshing,
  setRefreshError,
  setRememberMe,
  setLoginTime,
} = authSlice.actions

export const authReducer = authSlice.reducer

// ─── Auth Selectors ───────────────────────────────────────────────────────────
import type { RootState } from '../index'

export const selectCurrentUser      = (s: RootState) => s.auth.user
export const selectIsAuthenticated  = (s: RootState) => s.auth.isAuthenticated
export const selectIsInitializing   = (s: RootState) => s.auth.isInitializing
export const selectIsRefreshing     = (s: RootState) => s.auth.isRefreshing
export const selectAuthError        = (s: RootState) => s.auth.error
export const selectRefreshError     = (s: RootState) => s.auth.refreshError
export const selectUserRole         = (s: RootState) => s.auth.user?.role
export const selectUserPermissions  = (s: RootState) => s.auth.user?.permissions ?? []
export const selectRememberMe       = (s: RootState) => s.auth.rememberMe
export const selectLoginTime        = (s: RootState) => s.auth.loginTime
export const selectAuthTokens       = (s: RootState) => s.auth.tokens
