// ─── Spring Boot API Client ───────────────────────────────────────────────────
// Phase F2 – Dedicated Axios instance for Spring Boot backend
//
// ⚠️  API CLIENT ISOLATION RULES:
//     This client: Spring Boot only (auth, users, cases, courts, admin)
//     Base URL:    VITE_SPRING_API_URL (default http://localhost:8080/api/v1)
//
//     The existing apiClient.ts is reserved for FastAPI / AI endpoints.
//     DO NOT cross-use these clients.
import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
  type AxiosResponse,
} from 'axios'
import { v4 as uuid } from 'uuid'
import { APP_CONSTANTS } from '@/constants'
import { logger } from '@/core/logger'
import { eventBus } from '@/core/events'
import { normalizeApiError } from '@/api/errors'

const SPRING_API_URL = import.meta.env.VITE_SPRING_API_URL ?? 'http://localhost:8080/api/v1'

// ─── Create Axios Instance ────────────────────────────────────────────────────
export const springApiClient: AxiosInstance = axios.create({
  baseURL: SPRING_API_URL,
  timeout: APP_CONSTANTS.REQUEST_TIMEOUT,
  headers: {
    'Content-Type':  'application/json',
    'Accept':        'application/json',
    'X-App-Name':    APP_CONSTANTS.APP_NAME,
    'X-App-Version': APP_CONSTANTS.APP_VERSION,
    'X-Client':      'trustcourt-ui',
  },
})

// ─── Request Interceptor ──────────────────────────────────────────────────────
springApiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const correlationId = uuid()
    const requestId     = uuid()
    config.headers['X-Correlation-ID'] = correlationId
    config.headers['X-Request-ID']     = requestId

    logger.debug(`[SpringAPI →] ${config.method?.toUpperCase()} ${config.url}`, {
      correlationId, requestId,
    })

    eventBus.emit('api:request-start', { url: config.url, method: config.method })
    return config
  },
  (error: unknown) => {
    logger.error('[SpringAPI] Request error', error)
    return Promise.reject(error)
  }
)

// ─── Response Interceptor ─────────────────────────────────────────────────────
// NOTE: The 401 token-refresh interceptor is attached separately by authInterceptor.ts
//       to keep concerns cleanly separated. Do not add 401 logic here.
springApiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    logger.debug(`[SpringAPI ←] ${response.status} ${response.config.url}`)
    eventBus.emit('api:request-end', {
      url:    response.config.url,
      status: response.status,
    })
    return response
  },
  async (error: unknown) => {
    const normalizedError = normalizeApiError(error)
    logger.error('[SpringAPI ✕] Error', { message: normalizedError.message })
    eventBus.emit('api:error', { error: normalizedError })
    return Promise.reject(normalizedError)
  }
)

// ─── Request helpers ──────────────────────────────────────────────────────────
export const springApi = {
  get:    <T>(url: string, config?: AxiosRequestConfig) =>
    springApiClient.get<T>(url, config).then((r) => r.data),

  post:   <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    springApiClient.post<T>(url, data, config).then((r) => r.data),

  put:    <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    springApiClient.put<T>(url, data, config).then((r) => r.data),

  patch:  <T>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
    springApiClient.patch<T>(url, data, config).then((r) => r.data),

  delete: <T>(url: string, config?: AxiosRequestConfig) =>
    springApiClient.delete<T>(url, config).then((r) => r.data),
}
