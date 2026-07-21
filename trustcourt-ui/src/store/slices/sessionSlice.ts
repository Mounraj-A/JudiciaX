// ─── Redux Session Slice ──────────────────────────────────────────────────────
// Phase F2 – Session state: idle tracking, metadata, session lifecycle
import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { SessionState, SessionMetadata } from '@/types/auth/session'

const initialState: SessionState = {
  isIdle:         false,
  idleWarning:    false,
  sessionExpired: false,
  lastActivity:   Date.now(),
  metadata:       null,
}

const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    setIdle(state, action: PayloadAction<boolean>) {
      state.isIdle = action.payload
    },
    setIdleWarning(state, action: PayloadAction<boolean>) {
      state.idleWarning = action.payload
    },
    markSessionExpired(state) {
      state.sessionExpired = true
      state.isIdle         = true
    },
    updateLastActivity(state) {
      state.lastActivity = Date.now()
      state.isIdle       = false
      state.idleWarning  = false
    },
    setSessionMetadata(state, action: PayloadAction<SessionMetadata>) {
      state.metadata = action.payload
    },
    updateSessionMetadataActivity(state) {
      if (state.metadata) {
        state.metadata.lastActivity = Date.now()
      }
    },
    resetSession() {
      return { ...initialState, lastActivity: Date.now() }
    },
  },
})

export const {
  setIdle,
  setIdleWarning,
  markSessionExpired,
  updateLastActivity,
  setSessionMetadata,
  updateSessionMetadataActivity,
  resetSession,
} = sessionSlice.actions

export const sessionReducer = sessionSlice.reducer

// ─── Session Selectors ────────────────────────────────────────────────────────
import type { RootState } from '../index'

export const selectIsIdle          = (s: RootState) => s.session.isIdle
export const selectIdleWarning     = (s: RootState) => s.session.idleWarning
export const selectSessionExpired  = (s: RootState) => s.session.sessionExpired
export const selectLastActivity    = (s: RootState) => s.session.lastActivity
export const selectSessionMetadata = (s: RootState) => s.session.metadata
