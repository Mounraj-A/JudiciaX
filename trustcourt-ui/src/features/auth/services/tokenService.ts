// ─── Token Service ────────────────────────────────────────────────────────────
// Phase F2 – Token storage, refresh scheduling, and refresh execution
import type { AppDispatch } from '@/store'
import type { AuthTokens } from '@/types/auth'
import { tokenManager, storage } from '@/core/session'
import { APP_CONSTANTS }         from '@/constants'
import { isTokenValid }          from '@/core/token/tokenValidator'
import { dynamicRefreshTimer }   from '@/core/token/dynamicRefreshTimer'
import { refreshQueue }          from '@/core/token/tokenRefreshQueue'
import { refreshApi }            from '@/api/services/auth/authApi'
import { updateTokens, setRefreshing, setRefreshError } from '@/store/slices/authSlice'
import { auditService }          from './auditService'
import { handleAuthError }       from '../errors/authErrorHandler'
import { logger }                from '@/core/logger'

const KEY_REFRESH = `${APP_CONSTANTS.STORAGE_PREFIX}refresh_token`

class TokenServiceClass {
  private _dispatch: AppDispatch | null = null

  init(dispatch: AppDispatch): void {
    this._dispatch = dispatch
  }

  private get dispatch(): AppDispatch {
    if (!this._dispatch) throw new Error('[TokenService] Not initialised')
    return this._dispatch
  }

  /** Save tokens to the active storage strategy. */
  saveTokens(tokens: AuthTokens): void {
    tokenManager.saveTokens(tokens)
    logger.info('[TokenService] Tokens saved')
  }

  /** Clear all stored tokens. */
  clearTokens(): void {
    tokenManager.clearTokens()
    dynamicRefreshTimer.cancel()
    refreshQueue.reset()
    logger.info('[TokenService] Tokens cleared')
  }

  getAccessToken(): string | null {
    return tokenManager.getAccessToken()
  }

  getRefreshToken(): string | null {
    return storage.get<string>(KEY_REFRESH)
  }

  isExpired(): boolean {
    const token = this.getAccessToken()
    return !isTokenValid(token)
  }

  /**
   * Schedule the next token refresh using dynamic timing (85% of expiresIn).
   * @param expiresInMs - Token lifetime in ms from backend response
   */
  scheduleRefresh(expiresInMs: number): void {
    dynamicRefreshTimer.schedule(expiresInMs, () => {
      this.refresh().catch((err) => {
        logger.error('[TokenService] Scheduled refresh failed', err)
      })
    })
  }

  cancelRefresh(): void {
    dynamicRefreshTimer.cancel()
  }

  /**
   * Perform a token refresh.
   * Uses refreshQueue for concurrent-refresh protection.
   * Returns the new access token.
   */
  async refresh(): Promise<string> {
    return refreshQueue.enqueue(async () => {
      const refreshToken = this.getRefreshToken()
      if (!refreshToken) {
        throw handleAuthError(new Error('No refresh token available'))
      }

      this.dispatch(setRefreshing(true))
      logger.info('[TokenService] Refreshing token via Spring Boot')

      try {
        const response = await refreshApi(refreshToken)
        const data     = response.data

        const tokens: AuthTokens = {
          accessToken:  data.accessToken,
          refreshToken: data.refreshToken,
          expiresIn:    data.expiresIn,
          tokenType:    'Bearer',
        }

        this.saveTokens(tokens)
        this.dispatch(updateTokens(tokens))
        this.scheduleRefresh(data.expiresIn)
        auditService.onTokenRefresh()

        logger.info('[TokenService] Token refreshed successfully')
        return data.accessToken
      } catch (error) {
        const authError = handleAuthError(error)
        this.dispatch(setRefreshError(authError.message))
        throw authError
      }
    })
  }
}

export const tokenService = new TokenServiceClass()
