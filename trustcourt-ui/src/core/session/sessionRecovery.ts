// ─── Session Recovery ─────────────────────────────────────────────────────────
// Phase F2 – Restores authenticated session on app reload/startup
//
// Flow:
//   1. Check storage (local + session) for access + refresh tokens
//   2. Validate access token structure + expiry
//   3a. Valid: restore Redux auth state → resolve permissions → fetch profile
//   3b. Expired + refresh token: attempt silent refresh → then restore
//   3c. Refresh fails or no tokens: clear all state → route to /login
//   4. Dispatch setInitializing(false) in all paths
//   5. Emit app:ready
import type { AppDispatch } from '@/store'
import type { AuthUser, AuthTokens } from '@/types/auth'
import { tokenManager }      from '@/core/session'
import { validateToken }     from '@/core/token/tokenValidator'
import { decodeToken }       from '@/core/token/tokenDecoder'
import { setCredentials, clearCredentials, setInitializing } from '@/store/slices/authSlice'
import { clearPermissions }  from '@/store/slices/permissionSlice'
import { clearProfile }      from '@/store/slices/userSlice'
import { identityService }   from '@/features/auth/services/identityService'
import { permissionService } from '@/features/auth/services/permissionService'
import { profileService }    from '@/features/auth/services/profileService'
import { tokenService }      from '@/features/auth/services/tokenService'
import { eventBus }          from '@/core/events'
import { logger }            from '@/core/logger'

export async function recoverSession(dispatch: AppDispatch): Promise<boolean> {
  logger.info('[SessionRecovery] Starting session recovery...')

  const accessToken  = tokenManager.getAccessToken()
  const refreshToken = tokenManager.getRefreshToken()

  // ── Path 1: No tokens at all ────────────────────────────────────────────────
  if (!accessToken && !refreshToken) {
    logger.info('[SessionRecovery] No tokens found — fresh session')
    dispatch(setInitializing(false))
    eventBus.emit('app:ready')
    return false
  }

  // ── Path 2: Access token valid ───────────────────────────────────────────────
  const validation = validateToken(accessToken)
  if (validation.valid && accessToken) {
    logger.info('[SessionRecovery] Valid access token found — restoring session')
    try {
      await _restoreFromToken(dispatch, accessToken)
      return true
    } catch (error) {
      logger.error('[SessionRecovery] Profile fetch failed after valid token', error)
      _clearAll(dispatch)
      return false
    }
  }

  // ── Path 3: Access expired — try silent refresh ────────────────────────────
  if (refreshToken) {
    logger.info('[SessionRecovery] Access token expired — attempting silent refresh')
    try {
      tokenService.init(dispatch)
      const newAccessToken = await tokenService.refresh()
      await _restoreFromToken(dispatch, newAccessToken)
      return true
    } catch (error) {
      logger.warn('[SessionRecovery] Silent refresh failed — clearing session', error)
      _clearAll(dispatch)
      return false
    }
  }

  // ── Path 4: Nothing worked ───────────────────────────────────────────────────
  logger.info('[SessionRecovery] Could not recover session')
  _clearAll(dispatch)
  return false
}

async function _restoreFromToken(dispatch: AppDispatch, accessToken: string): Promise<void> {
  // Decode JWT for role/email (display only — not authoritative)
  const decoded  = decodeToken(accessToken)

  // Fetch authoritative user profile from backend
  profileService.init(dispatch)
  identityService  // no dispatch needed
  const profile = await profileService.fetchProfile()

  const authUser: AuthUser = {
    id:           profile.id,
    name:         profile.fullName,
    email:        profile.email,
    role:         profile.role,
    permissions:  profile.permissions,
    isActive:     profile.isActive,
    lastLoginAt:  profile.lastLoginAt,
    phoneNumber:  profile.phoneNumber,
    emailVerified:profile.emailVerified,
    accountStatus:profile.accountStatus,
  }

  const tokens: AuthTokens = {
    accessToken,
    refreshToken: tokenManager.getRefreshToken() ?? '',
    expiresIn:    0,
    tokenType:    'Bearer',
  }

  // Resolve permissions from role (single write to permissionSlice)
  permissionService.init(dispatch)
  permissionService.resolvePermissions(authUser.role)

  // Set Redux auth state
  dispatch(setCredentials({ user: authUser, tokens }))

  // Schedule next refresh if we know expiry
  if (decoded?.exp) {
    const remainingMs = (decoded.exp * 1000) - Date.now()
    if (remainingMs > 0) tokenService.scheduleRefresh(remainingMs)
  }

  dispatch(setInitializing(false))
  eventBus.emit('app:ready')
  logger.info('[SessionRecovery] Session restored', { role: authUser.role })
}

function _clearAll(dispatch: AppDispatch): void {
  tokenManager.clearTokens()
  dispatch(clearCredentials())
  dispatch(clearPermissions())
  dispatch(clearProfile())
  dispatch(setInitializing(false))
  eventBus.emit('app:ready')
}
