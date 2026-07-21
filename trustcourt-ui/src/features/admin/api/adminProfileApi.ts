// ─── Admin Profile API ────────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface UserProfileResponse {
  uuid: string
  username: string
  email: string
  fullName: string
  firstName: string
  lastName: string
  mobile: string
  role: string
  accountStatus: string
  profileCompletionPercent: number
  emailVerified: boolean
  mobileVerified: boolean
  createdAt: string
  updatedAt: string
}

export interface UpdateProfileRequest {
  firstName: string
  lastName: string
  mobile?: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

export interface SessionResponse {
  sessionUuid: string
  ipAddress: string
  userAgent: string
  deviceType: string
  isCurrentSession: boolean
  createdAt: string
  lastActivityAt: string
  expiresAt: string
}

export const adminProfileApi = {
  getMyProfile: () =>
    springApi.get<ApiResponse<UserProfileResponse>>('/users/me'),

  updateMyProfile: (data: UpdateProfileRequest) =>
    springApi.put<ApiResponse<UserProfileResponse>>('/users/me', data),

  changePassword: (data: ChangePasswordRequest) =>
    springApi.put<ApiResponse<void>>('/users/change-password', data),

  getMySessions: () =>
    springApi.get<ApiResponse<SessionResponse[]>>('/users/me/sessions'),

  revokeMySession: (sessionUuid: string) =>
    springApi.delete<ApiResponse<void>>(`/users/me/sessions/${sessionUuid}`),

  revokeAllMySessions: () =>
    springApi.delete<ApiResponse<void>>('/users/me/sessions'),

  updatePrivacySettings: (settings: Record<string, boolean>) =>
    springApi.put<ApiResponse<void>>('/users/me/privacy-settings', settings),
}
