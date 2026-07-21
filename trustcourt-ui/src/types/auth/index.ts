// ─── Authentication & Identity Types ─────────────────────────────────────────
// Phase F2 – Enterprise Authentication Platform

// ─── User Roles ───────────────────────────────────────────────────────────────
/**
 * All supported user roles in TrustCourt.
 * Backend maps these to Spring Security ROLE_ prefixed authorities.
 * Adding a new role requires only updating ROLE_REGISTRY in core/permissions.
 */
export type UserRole =
  | 'JUDGE'
  | 'ADVOCATE'
  | 'CLERK'
  | 'ADMIN'
  | 'SUPER_ADMIN'
  | 'AUDITOR'
  | 'AI_OPERATOR'
  | 'RESEARCHER'
  | 'VIEWER'

// ─── Authenticated User ───────────────────────────────────────────────────────
/** The core auth user — sourced from backend login response. Single source of truth in authSlice. */
export interface AuthUser {
  id:           string
  name:         string
  email:        string
  role:         UserRole
  permissions:  string[]
  courtId?:     string
  courtName?:   string
  avatarUrl?:   string
  isActive:     boolean
  lastLoginAt?: string
  phoneNumber?: string
  emailVerified?: boolean
  accountStatus?: 'ACTIVE' | 'SUSPENDED' | 'PENDING'
}

// ─── Auth Tokens ──────────────────────────────────────────────────────────────
export interface AuthTokens {
  accessToken:  string
  refreshToken: string
  expiresIn:    number   // milliseconds — used by DynamicRefreshTimer
  tokenType:    'Bearer'
  loginTime?:   string   // ISO timestamp from backend
}

// ─── Auth State ───────────────────────────────────────────────────────────────
export interface AuthState {
  user:            AuthUser | null
  tokens:          AuthTokens | null
  isAuthenticated: boolean
  isInitializing:  boolean
  isRefreshing:    boolean
  refreshError:    string | null
  rememberMe:      boolean
  loginTime:       string | null
  error:           string | null
}

// ─── Login ────────────────────────────────────────────────────────────────────
export interface LoginCredentials {
  email:       string
  password:    string
  rememberMe?: boolean
}

export interface LoginResult {
  user:   AuthUser
  tokens: AuthTokens
}

// ─── Registration ─────────────────────────────────────────────────────────────
/**
 * Registration request — Development/testing phase only.
 *
 * ⚠️  PRODUCTION NOTE: Public registration is DISABLED in production deployments.
 *      User creation is handled exclusively by the Admin module (Phase F5+).
 *      Disable by setting VITE_FEATURE_FLAGS={"REGISTRATION":false}
 *      or VITE_APP_ENV=production (auto-disabled).
 *      No code changes required to disable registration.
 */
export interface RegisterRequest {
  fullName:        string
  email:           string
  password:        string
  confirmPassword: string
  role:            UserRole
  phoneNumber?:    string
}

export interface RegisterResult {
  userId: string
  email:  string
  message: string
}

// ─── Password Reset ───────────────────────────────────────────────────────────
export interface ForgotPasswordRequest {
  email: string
}

export interface ResetPasswordRequest {
  token:           string
  newPassword:     string
  confirmPassword: string
}

// ─── Refresh Token ────────────────────────────────────────────────────────────
export interface RefreshTokenRequest {
  refreshToken: string
}

// ─── Auth Error ───────────────────────────────────────────────────────────────
export enum AuthErrorCode {
  INVALID_CREDENTIALS    = 'INVALID_CREDENTIALS',
  EXPIRED_TOKEN          = 'EXPIRED_TOKEN',
  UNAUTHORIZED           = 'UNAUTHORIZED',
  FORBIDDEN              = 'FORBIDDEN',
  SESSION_EXPIRED        = 'SESSION_EXPIRED',
  CONCURRENT_LOGIN       = 'CONCURRENT_LOGIN',
  ACCOUNT_LOCKED         = 'ACCOUNT_LOCKED',
  PASSWORD_EXPIRED       = 'PASSWORD_EXPIRED',
  REFRESH_FAILED         = 'REFRESH_FAILED',
  NETWORK_ERROR          = 'NETWORK_ERROR',
  REGISTRATION_DISABLED  = 'REGISTRATION_DISABLED',
  UNKNOWN                = 'UNKNOWN',
}
