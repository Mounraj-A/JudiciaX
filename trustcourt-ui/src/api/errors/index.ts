// ─── API Error Types & Normalizer ─────────────────────────────────────────────
import type { ApiError } from '@/types/api'
import { logger } from '@/core/logger'
import { eventBus } from '@/core/events'

export class AppError extends Error {
  readonly code:      string
  readonly status:    number
  readonly details?:  Record<string, string[]>
  readonly requestId?:string

  constructor(error: ApiError) {
    super(error.message)
    this.name      = 'AppError'
    this.code      = error.code
    this.status    = error.status
    this.details   = error.details
    this.requestId = error.requestId
  }
}

export class NetworkError extends Error {
  constructor(message = 'Network connection error') {
    super(message)
    this.name = 'NetworkError'
  }
}

export class AuthError extends Error {
  constructor(message = 'Authentication required') {
    super(message)
    this.name = 'AuthError'
  }
}

export class ForbiddenError extends Error {
  constructor(message = 'Insufficient permissions') {
    super(message)
    this.name = 'ForbiddenError'
  }
}

/** Normalizes raw Axios/network errors into typed AppError instances */
export function normalizeApiError(error: unknown): AppError | NetworkError | AuthError | ForbiddenError {
  // Axios error with response
  if (
    error &&
    typeof error === 'object' &&
    'response' in error &&
    (error as { response?: { data?: unknown; status?: number } }).response
  ) {
    const axiosErr = error as { response: { data: { detail?: string; message?: string; code?: string; requestId?: string }; status: number } }
    const { data, status } = axiosErr.response

    if (status === 401) {
      eventBus.emit('auth:unauthorized')
      return new AuthError(data?.detail ?? data?.message ?? 'Unauthorized')
    }
    if (status === 403) return new ForbiddenError(data?.detail ?? data?.message ?? 'Forbidden')

    let errorMessage = data?.detail ?? data?.message ?? 'An error occurred'
    if (status === 400 && Array.isArray((data as any)?.data)) {
        errorMessage = (data as any).data.join(', ')
    }

    return new AppError({
      code:      data?.code      ?? `HTTP_${status}`,
      message:   errorMessage,
      status,
      requestId: data?.requestId,
    })
  }

  // No response (network error)
  if (
    error &&
    typeof error === 'object' &&
    'request' in error
  ) {
    logger.error('[API] Network error — no response received', error)
    return new NetworkError()
  }

  // Unknown
  logger.error('[API] Unknown error', error)
  return new AppError({ code: 'UNKNOWN', message: 'An unexpected error occurred', status: 0 })
}

/** Human-readable message for a normalized error */
export function getErrorMessage(error: unknown): string {
  if (error instanceof AppError)    return error.message
  if (error instanceof NetworkError)return 'Network unavailable. Check your connection.'
  if (error instanceof AuthError)   return 'Your session has expired. Please log in again.'
  if (error instanceof ForbiddenError) return 'You do not have permission to perform this action.'
  if (error instanceof Error)        return error.message
  return 'An unexpected error occurred.'
}
