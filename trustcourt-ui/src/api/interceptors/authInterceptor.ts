// ─── Auth Interceptor ─────────────────────────────────────────────────────────
// Phase F2 – Attaches token injection + 401 refresh logic to springApiClient
//
// This fills the "TODO: Phase F2" stub that was in apiClient.ts.
// It is applied to springApiClient only — not the FastAPI client.

import type { InternalAxiosRequestConfig, AxiosError } from 'axios'
import { springApiClient } from '@/api/client/springApiClient'
import { refreshQueue }    from '@/core/token/tokenRefreshQueue'
import { logger }          from '@/core/logger'
import { eventBus }        from '@/core/events'

// ─── Extended Request Config ──────────────────────────────────────────────────
interface RetryableConfig extends InternalAxiosRequestConfig {
  _retry?: boolean
}

// ─── Token getter/setter injected at setup time ───────────────────────────────
// Using a late-binding pattern to avoid circular imports with tokenService
let _getAccessToken:  () => string | null = () => null
let _doRefresh:       () => Promise<string> = () => Promise.reject(new Error('Not initialized'))
let _onRefreshFailed: () => void = () => {}

export interface AuthInterceptorConfig {
  getAccessToken:  () => string | null
  doRefresh:       () => Promise<string>
  onRefreshFailed: () => void
}

/**
 * Initialize and attach the auth interceptor to springApiClient.
 * Called once by AppInitializer after services are wired.
 */
export function setupAuthInterceptor(config: AuthInterceptorConfig): void {
  _getAccessToken  = config.getAccessToken
  _doRefresh       = config.doRefresh
  _onRefreshFailed = config.onRefreshFailed

  // ── Request: Inject Bearer token ──────────────────────────────────────────
  springApiClient.interceptors.request.use(
    (cfg: InternalAxiosRequestConfig) => {
      const token = _getAccessToken()
      if (token) {
        cfg.headers['Authorization'] = `Bearer ${token}`
      }
      return cfg
    },
    (error) => Promise.reject(error)
  )

  // ── Response: Handle 401 with refresh + retry ─────────────────────────────
  springApiClient.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as RetryableConfig | undefined

      if (
        error.response?.status === 401 &&
        originalRequest &&
        !originalRequest._retry
      ) {
        originalRequest._retry = true
        logger.warn('[AuthInterceptor] 401 detected — attempting token refresh')

        try {
          const newToken = await refreshQueue.enqueue(_doRefresh)
          originalRequest.headers['Authorization'] = `Bearer ${newToken}`
          return springApiClient(originalRequest)
        } catch (refreshError) {
          logger.error('[AuthInterceptor] Refresh failed — clearing session', refreshError)
          _onRefreshFailed()
          eventBus.emit('auth:unauthorized')
          return Promise.reject(refreshError)
        }
      }

      return Promise.reject(error)
    }
  )

  logger.info('[AuthInterceptor] Attached to springApiClient')
}
