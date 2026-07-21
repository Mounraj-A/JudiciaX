// ─── FastAPI / AI Service Client ──────────────────────────────────────────────
// ⚠️  THIS CLIENT IS RESERVED FOR FastAPI AI ENDPOINTS ONLY.
//     (OCR, NLP, JPI, CTS, XAI, JDSE, Governance — Phase F4+)
//
//     Auth, user, case, and court API calls → use springApiClient.ts
//     Bearer token injection + 401 refresh → handled in authInterceptor.ts on springApiClient
import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
  type AxiosResponse,
} from 'axios'
import { v4 as uuid } from 'uuid'
import { appConfig } from '@/config/env'
import { APP_CONSTANTS } from '@/constants'
import { tokenManager } from '@/core/session'
import { logger } from '@/core/logger'
import { eventBus } from '@/core/events'
import { normalizeApiError } from '@/api/errors'

// ─── Create Axios Instance ────────────────────────────────────────────────────
const apiClient: AxiosInstance = axios.create({
  baseURL:        appConfig.apiUrl,
  timeout:        APP_CONSTANTS.REQUEST_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
    'Accept':       'application/json',
    'X-App-Name':   APP_CONSTANTS.APP_NAME,
    'X-App-Version':APP_CONSTANTS.APP_VERSION,
  },
})

// ─── Request Interceptor ──────────────────────────────────────────────────────
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Correlation ID — unique per request, for tracing across logs
    const correlationId = uuid()
    const requestId     = uuid()
    config.headers['X-Correlation-ID'] = correlationId
    config.headers['X-Request-ID']     = requestId

    // JWT Bearer token injection
    const token = tokenManager.getAccessToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    logger.debug(`[API →] ${config.method?.toUpperCase()} ${config.url}`, {
      correlationId,
      requestId,
      params: config.params,
    })

    // Emit API start event
    eventBus.emit('api:request-start', { url: config.url, method: config.method })
    return config
  },
  (error: unknown) => {
    logger.error('[API] Request interceptor error', error)
    return Promise.reject(error)
  }
)

// ─── Response Interceptor ────────────────────────────────────────────────────
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    logger.debug(`[API ←] ${response.status} ${response.config.url}`)
    eventBus.emit('api:request-end', {
      url:    response.config.url,
      status: response.status,
    })
    return response
  },
  (error: unknown) => {
    const normalizedError = normalizeApiError(error)
    logger.error('[API ✕] Error', { message: normalizedError.message })
    eventBus.emit('api:error', { error: normalizedError })
    return Promise.reject(normalizedError)
  }
)

// ─── Request helpers ──────────────────────────────────────────────────────────
export const api = {
  get:    <T>(url: string, config?: AxiosRequestConfig) =>
    apiClient.get<T>(url, config).then((r) => r.data),

  post:   <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    apiClient.post<T>(url, data, config).then((r) => r.data),

  put:    <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    apiClient.put<T>(url, data, config).then((r) => r.data),

  patch:  <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    apiClient.patch<T>(url, data, config).then((r) => r.data),

  delete: <T>(url: string, config?: AxiosRequestConfig) =>
    apiClient.delete<T>(url, config).then((r) => r.data),
}

export { apiClient }
