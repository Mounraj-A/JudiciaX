// ─── Auth Error ───────────────────────────────────────────────────────────────
// Phase F2 – Typed error model for all authentication failures
import { AuthErrorCode } from '@/types/auth'

export { AuthErrorCode }

/** Human-readable messages mapped from error codes. */
export const AUTH_ERROR_MESSAGES: Record<AuthErrorCode, string> = {
  [AuthErrorCode.INVALID_CREDENTIALS]:   'Invalid email or password. Please try again.',
  [AuthErrorCode.EXPIRED_TOKEN]:         'Your session has expired. Please log in again.',
  [AuthErrorCode.UNAUTHORIZED]:          'You are not authorised to perform this action.',
  [AuthErrorCode.FORBIDDEN]:             'You do not have permission to access this resource.',
  [AuthErrorCode.SESSION_EXPIRED]:       'Your session has timed out due to inactivity.',
  [AuthErrorCode.CONCURRENT_LOGIN]:      'Your account was signed in from another device.',
  [AuthErrorCode.ACCOUNT_LOCKED]:        'Your account has been locked. Contact your administrator.',
  [AuthErrorCode.PASSWORD_EXPIRED]:      'Your password has expired. Please reset it.',
  [AuthErrorCode.REFRESH_FAILED]:        'Unable to renew your session. Please log in again.',
  [AuthErrorCode.NETWORK_ERROR]:         'Unable to connect. Please check your internet connection.',
  [AuthErrorCode.REGISTRATION_DISABLED]: 'Registration is not available in this environment.',
  [AuthErrorCode.UNKNOWN]:               'An unexpected error occurred. Please try again.',
}

// ─── AuthError Class ──────────────────────────────────────────────────────────
export class AuthError extends Error {
  public readonly code:       AuthErrorCode
  public readonly statusCode: number | undefined
  public readonly isAuthError = true

  constructor(
    code:        AuthErrorCode,
    message?:    string,
    statusCode?: number
  ) {
    super(message ?? AUTH_ERROR_MESSAGES[code])
    this.name       = 'AuthError'
    this.code       = code
    this.statusCode = statusCode
    Object.setPrototypeOf(this, AuthError.prototype)
  }

  get userMessage(): string {
    return AUTH_ERROR_MESSAGES[this.code]
  }
}

export function isAuthError(error: unknown): error is AuthError {
  return error instanceof AuthError
}
