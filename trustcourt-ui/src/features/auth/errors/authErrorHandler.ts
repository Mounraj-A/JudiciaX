// ─── Auth Error Handler ───────────────────────────────────────────────────────
// Phase F2 – Maps HTTP/Axios errors to typed AuthError
import type { AxiosError } from 'axios'
import { AuthError, AuthErrorCode } from './AuthError'
import { logger } from '@/core/logger'

interface SpringErrorResponse {
  success:   boolean
  message?:  string
  errorCode?: string
  status?:   number
}

/**
 * Normalise any thrown value into a typed AuthError.
 * Handles Axios errors, AuthErrors (pass-through), and unknown errors.
 */
export function handleAuthError(error: unknown): AuthError {
  // Already an AuthError — pass through
  if (error instanceof AuthError) return error

  const axiosError = error as AxiosError<SpringErrorResponse>

  if (axiosError.response) {
    const { status, data } = axiosError.response
    const backendCode      = data?.errorCode ?? ''
    const backendMessage   = data?.message

    switch (status) {
      case 400:
        if (backendCode === 'PASSWORD_EXPIRED')
          return new AuthError(AuthErrorCode.PASSWORD_EXPIRED, backendMessage, status)
        return new AuthError(AuthErrorCode.INVALID_CREDENTIALS, backendMessage, status)

      case 401:
        if (backendCode === 'TOKEN_EXPIRED' || backendCode === 'EXPIRED_TOKEN')
          return new AuthError(AuthErrorCode.EXPIRED_TOKEN, backendMessage, status)
        if (backendCode === 'SESSION_EXPIRED')
          return new AuthError(AuthErrorCode.SESSION_EXPIRED, backendMessage, status)
        return new AuthError(AuthErrorCode.UNAUTHORIZED, backendMessage, status)

      case 403:
        if (backendCode === 'ACCOUNT_LOCKED')
          return new AuthError(AuthErrorCode.ACCOUNT_LOCKED, backendMessage, status)
        if (backendCode === 'CONCURRENT_LOGIN')
          return new AuthError(AuthErrorCode.CONCURRENT_LOGIN, backendMessage, status)
        return new AuthError(AuthErrorCode.FORBIDDEN, backendMessage, status)

      case 422:
        return new AuthError(AuthErrorCode.INVALID_CREDENTIALS, backendMessage, status)

      case 429:
        return new AuthError(AuthErrorCode.UNAUTHORIZED, 'Too many attempts. Please wait.', status)

      default:
        logger.warn('[AuthErrorHandler] Unhandled HTTP status', { status, backendCode })
        return new AuthError(AuthErrorCode.UNKNOWN, backendMessage, status)
    }
  }

  if (axiosError.code === 'ECONNABORTED' || axiosError.code === 'ERR_NETWORK') {
    return new AuthError(AuthErrorCode.NETWORK_ERROR)
  }

  logger.error('[AuthErrorHandler] Unhandled error type', error)
  return new AuthError(AuthErrorCode.UNKNOWN)
}
