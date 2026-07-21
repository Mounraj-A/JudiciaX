// ─── Application Initializer Component — Phase F2 ─────────────────────────────
// Replaced Phase F1 stub with SessionRecovery.
// Sequence:
//   1. setupAuthInterceptor → attach Bearer + 401 refresh to springApiClient
//   2. recoverSession → check storage → validate token → restore Redux state
//   3. idleTimer + event bus wiring
//   4. setInitializing(false) → app:ready (emitted by recoverSession)
import React, { useEffect, useRef } from 'react'
import { useAppDispatch }      from '@/store'
import { authService }         from '@/features/auth/services/authService'
import { tokenService }        from '@/features/auth/services/tokenService'
import { sessionService }      from '@/features/auth/services/sessionService'
import { permissionService }   from '@/features/auth/services/permissionService'
import { profileService }      from '@/features/auth/services/profileService'
import { recoverSession }      from '@/core/session/sessionRecovery'
import { setupAuthInterceptor } from '@/api/interceptors/authInterceptor'
import { tokenManager }        from '@/core/session'
import { logger }              from '@/core/logger'
import { AppLoader }           from '@/app/AppLoader'
import { useAppSelector }      from '@/store'
import { selectIsInitializing } from '@/store/slices/authSlice'

export function AppInitializer({ children }: { children: React.ReactNode }) {
  const dispatch        = useAppDispatch()
  const isInitializing  = useAppSelector(selectIsInitializing)
  const initialized     = useRef(false)

  useEffect(() => {
    if (initialized.current) return
    initialized.current = true

    logger.info('[AppInitializer] Phase F2 — Bootstrapping auth platform...')

    // 1. Wire all service dispatch dependencies
    authService.init(dispatch)
    tokenService.init(dispatch)
    sessionService.init(dispatch)
    permissionService.init(dispatch)
    profileService.init(dispatch)

    // 2. Attach auth interceptor to springApiClient
    setupAuthInterceptor({
      getAccessToken:  () => tokenManager.getAccessToken(),
      doRefresh:       () => tokenService.refresh(),
      onRefreshFailed: () => authService.logout().catch(() => {}),
    })

    // 3. Recover session (validates tokens, restores Redux state, emits app:ready)
    recoverSession(dispatch).then((recovered) => {
      logger.info(`[AppInitializer] Session recovery ${recovered ? 'succeeded' : 'no session found'}`)
    }).catch((err) => {
      logger.error('[AppInitializer] Session recovery error', err)
    })
  }, [dispatch])

  if (isInitializing) {
    return <AppLoader message="Bootstrapping TrustCourt Enterprise Foundation..." />
  }

  return <>{children}</>
}
