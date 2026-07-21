// ─── Session Context ──────────────────────────────────────────────────────────
// Phase F2 – Idle state, session metadata, extend/expire session
// Reads from Redux sessionSlice — owns no state.
// Also watches sessionExpired flag and triggers auto-logout.
import React, { createContext, useContext, useCallback, useEffect } from 'react'
import { useAppSelector, useAppDispatch } from '@/store'
import {
  selectIsIdle, selectIdleWarning, selectSessionExpired,
  selectLastActivity, selectSessionMetadata, updateLastActivity,
} from '@/store/slices/sessionSlice'
import { useAuthContext } from './AuthContext'
import { idleTimer }      from '@/core/session'
import type { SessionMetadata } from '@/types/auth/session'

export interface SessionContextValue {
  isIdle:         boolean
  idleWarning:    boolean
  sessionExpired: boolean
  lastActivity:   number
  metadata:       SessionMetadata | null
  extendSession:  () => void
}

const SessionContext = createContext<SessionContextValue | null>(null)

export function SessionProvider({ children }: { children: React.ReactNode }) {
  const dispatch       = useAppDispatch()
  const isIdle         = useAppSelector(selectIsIdle)
  const idleWarning    = useAppSelector(selectIdleWarning)
  const sessionExpired = useAppSelector(selectSessionExpired)
  const lastActivity   = useAppSelector(selectLastActivity)
  const metadata       = useAppSelector(selectSessionMetadata)
  const { logout }     = useAuthContext()

  // Auto-logout when session expires
  useEffect(() => {
    if (sessionExpired) {
      logout().catch(() => {})
    }
  }, [sessionExpired, logout])

  const extendSession = useCallback(() => {
    dispatch(updateLastActivity())
    idleTimer.start()
  }, [dispatch])

  return (
    <SessionContext.Provider value={{
      isIdle, idleWarning, sessionExpired, lastActivity, metadata, extendSession,
    }}>
      {children}
    </SessionContext.Provider>
  )
}

export function useSessionContext(): SessionContextValue {
  const ctx = useContext(SessionContext)
  if (!ctx) throw new Error('useSessionContext must be used within <SessionProvider>')
  return ctx
}
