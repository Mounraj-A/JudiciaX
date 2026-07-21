// ─── Auth API — Spring Boot Integration ──────────────────────────────────────
// Phase F2 – Pure typed API functions for Spring Boot /api/v1/auth/* endpoints
//
// These are thin wrappers around springApiClient. No business logic lives here.
// All functions correspond 1-to-1 with backend controller endpoints.

import { springApiClient } from '@/api/client/springApiClient'
import type {
  LoginCredentials,
  RegisterRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '@/types/auth'

// ─── Backend Response Shapes ──────────────────────────────────────────────────
// Mirrors Spring Boot LoginResponse, RefreshTokenResponse, UserProfileResponse

export interface ApiLoginResponse {
  accessToken:  string
  refreshToken: string
  tokenType:    string
  expiresIn:    number    // milliseconds
  user:         ApiUserProfileResponse
  loginTime:    string    // ISO timestamp from backend
}

export interface ApiRefreshTokenResponse {
  accessToken:  string
  refreshToken: string
  tokenType:    string
  expiresIn:    number
}

export interface ApiUserProfileResponse {
  uuid:                     string
  fullName:                 string
  email:                    string
  phoneNumber?:             string
  role:                     string    // e.g. "ROLE_JUDGE" — normalised to "JUDGE" in identityService
  accountStatus:            string
  emailVerified:            boolean
  mobileVerified?:          boolean
  profileCompletionPercent: number
  permissions:              string[]
  lastLogin?:               string
  createdAt?:               string
}

export interface ApiRegisterResponse {
  userId:  string
  email:   string
  message: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data:    T
  timestamp: string
}

// ─── Auth Endpoints ───────────────────────────────────────────────────────────

/** POST /api/v1/auth/login */
export async function loginApi(
  credentials: LoginCredentials
): Promise<ApiResponse<ApiLoginResponse>> {
  const response = await springApiClient.post<ApiResponse<ApiLoginResponse>>(
    '/auth/login',
    { email: credentials.email, password: credentials.password }
  )
  return response.data
}

/** POST /api/v1/auth/refresh */
export async function refreshApi(
  refreshToken: string
): Promise<ApiResponse<ApiRefreshTokenResponse>> {
  const response = await springApiClient.post<ApiResponse<ApiRefreshTokenResponse>>(
    '/auth/refresh',
    { refreshToken }
  )
  return response.data
}

/** POST /api/v1/auth/logout */
export async function logoutApi(authHeader?: string): Promise<void> {
  await springApiClient.post('/auth/logout', null, {
    headers: authHeader ? { Authorization: authHeader } : undefined,
  })
}

/** GET /api/v1/users/me */
export async function getCurrentUserApi(): Promise<ApiResponse<ApiUserProfileResponse>> {
  const response = await springApiClient.get<ApiResponse<ApiUserProfileResponse>>('/users/me')
  return response.data
}

/**
 * POST /api/v1/auth/register
 *
 * ⚠️  Development/testing phase only.
 *     In production: VITE_FEATURE_FLAGS={"REGISTRATION":false} disables this endpoint call.
 *     User creation in production is handled by the Admin module (Phase F5+).
 */
export async function registerApi(
  request: RegisterRequest
): Promise<ApiResponse<ApiRegisterResponse>> {
  const response = await springApiClient.post<ApiResponse<ApiRegisterResponse>>(
    '/auth/register',
    request
  )
  return response.data
}

/** POST /api/v1/auth/forgot-password */
export async function forgotPasswordApi(
  request: ForgotPasswordRequest
): Promise<void> {
  await springApiClient.post('/auth/forgot-password', request)
}

/** POST /api/v1/auth/reset-password */
export async function resetPasswordApi(
  request: ResetPasswordRequest
): Promise<void> {
  await springApiClient.post('/auth/reset-password', request)
}
