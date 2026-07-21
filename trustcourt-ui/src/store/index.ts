// ─── Redux Store — Enterprise Architecture ────────────────────────────────────
// Phase F2 – Auth platform state registered
import { configureStore } from '@reduxjs/toolkit'
import type { TypedUseSelectorHook } from 'react-redux'
import { useDispatch, useSelector } from 'react-redux'
import { authReducer }         from './slices/authSlice'
import { uiReducer }           from './slices/uiSlice'
import { notificationReducer } from './slices/notificationSlice'
import { sessionReducer }      from './slices/sessionSlice'
import { userReducer }         from './slices/userSlice'
import { permissionReducer }   from './slices/permissionSlice'
import { logger } from '@/core/logger'

// ─── Logger Middleware ────────────────────────────────────────────────────────
const reduxLoggerMiddleware = (_store: { getState: () => unknown }) =>
  (next: (action: unknown) => unknown) =>
  (action: unknown) => {
    if (import.meta.env.DEV) {
      const actionObj = action as { type?: string }
      logger.debug(`[Redux] ${actionObj.type ?? 'unknown'}`)
    }
    return next(action)
  }

// ─── Root Store ───────────────────────────────────────────────────────────────
export const store = configureStore({
  reducer: {
    auth:          authReducer,
    session:       sessionReducer,
    user:          userReducer,
    permission:    permissionReducer,
    ui:            uiReducer,
    notifications: notificationReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions:      ['ui/setGlobalLoading'],
        ignoredActionsPaths: [],
        ignoredPaths:        [],
      },
    }).concat(reduxLoggerMiddleware),
  devTools: import.meta.env.DEV,
})

// ─── Typed exports ────────────────────────────────────────────────────────────
export type RootState   = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

export const useAppDispatch: () => AppDispatch              = useDispatch
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector
