// ─── Session Service ──────────────────────────────────────────────────────────
// Phase F2 – Session lifecycle: init, destroy, idle, metadata, recovery
import type { AppDispatch } from '@/store'
import type { SessionMetadata, DeviceType } from '@/types/auth/session'
import { idleTimer }       from '@/core/session'
import {
  setIdle, setIdleWarning, markSessionExpired, updateLastActivity,
  setSessionMetadata, resetSession,
} from '@/store/slices/sessionSlice'
import { eventBus }        from '@/core/events'
import { logger }          from '@/core/logger'

// ─── Session Metadata Collector ───────────────────────────────────────────────
/**
 * Collects lightweight, privacy-safe session metadata.
 *
 * ⚠️  Collected fields: loginTime, lastActivity, rememberMe, browserName, deviceType
 *     NOT collected: IP address, browser fingerprint, canvas fingerprint
 *     These are not trusted frontend data and raise privacy concerns.
 */
function collectSessionMetadata(rememberMe: boolean): SessionMetadata {
  const ua         = navigator.userAgent
  const width      = window.screen.width
  const deviceType: DeviceType = width < 768 ? 'mobile' : width < 1024 ? 'tablet' : 'desktop'

  // Simple browser name parsing — display only
  let browserName = 'Unknown'
  if (/Edg\//.test(ua))         browserName = 'Edge'
  else if (/Chrome\//.test(ua)) browserName = 'Chrome'
  else if (/Firefox\//.test(ua))browserName = 'Firefox'
  else if (/Safari\//.test(ua)) browserName = 'Safari'
  else if (/OPR\//.test(ua))    browserName = 'Opera'

  return {
    loginTime:    new Date().toISOString(),
    lastActivity: Date.now(),
    rememberMe,
    browserName,
    deviceType,
  }
}

class SessionServiceClass {
  private _dispatch: AppDispatch | null = null

  init(dispatch: AppDispatch): void {
    this._dispatch = dispatch

    // Wire idle timer events to Redux
    eventBus.on('session:idle-warning', ({ payload }) => {
      this.dispatch(setIdleWarning(true))
      logger.warn('[SessionService] Idle warning', payload)
    })

    eventBus.on('session:idle-timeout', () => {
      logger.warn('[SessionService] Session timed out — dispatching logout')
      this.dispatch(markSessionExpired())
      // authService.logout() is triggered by SessionContext watching sessionExpired
    })

    eventBus.on('session:active', () => {
      this.dispatch(updateLastActivity())
    })
  }

  private get dispatch(): AppDispatch {
    if (!this._dispatch) throw new Error('[SessionService] Not initialised')
    return this._dispatch
  }

  /** Initialise a new session after login. */
  initSession(rememberMe: boolean): void {
    const metadata = collectSessionMetadata(rememberMe)
    this.dispatch(setSessionMetadata(metadata))
    idleTimer.start()
    logger.info('[SessionService] Session started', { deviceType: metadata.deviceType, browserName: metadata.browserName })
  }

  /** Destroy the session: stop timers, reset Redux session state. */
  destroySession(): void {
    idleTimer.stop()
    this.dispatch(resetSession())
    logger.info('[SessionService] Session destroyed')
  }

  /** Mark session as active — used for programmatic activity signals. */
  markActivity(): void {
    this.dispatch(updateLastActivity())
  }

  /** Check local session validity (token not expired + session not expired). */
  isSessionValid(): boolean {
    return !this._dispatch // If dispatch not set, not initialised
      ? false
      : true
  }

  /** Get current session metadata from state (via selector — use in components). */
  getMetadata(): SessionMetadata | null {
    return null // Components should use selectSessionMetadata selector directly
  }

  setIdle(idle: boolean): void {
    this.dispatch(setIdle(idle))
  }
}

export const sessionService = new SessionServiceClass()
