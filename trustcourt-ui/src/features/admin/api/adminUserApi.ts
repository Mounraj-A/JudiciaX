// ─── Admin User API ───────────────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

// ── Response Types ─────────────────────────────────────────────────────────────
export interface UserResponse {
  uuid: string
  username: string
  email: string
  fullName: string
  role: string
  accountStatus: string
  isLocked: boolean
  isDeleted: boolean
  emailVerified: boolean
  profileCompletionPercent: number
  failedLoginAttempts: number
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export type UserRole = 'ADMIN' | 'SUPER_ADMIN' | 'JUDGE' | 'CLERK' | 'ADVOCATE' | 'RESEARCHER' | 'VIEWER'

// ── API Functions ──────────────────────────────────────────────────────────────
export const adminUserApi = {
  listUsers: (page = 0, size = 20, role?: string) =>
    springApi.get<ApiResponse<PageResponse<UserResponse>>>('/admin/users', {
      params: { page, size, ...(role ? { role } : {}) },
    }),

  getUserByUuid: (uuid: string) =>
    springApi.get<ApiResponse<UserResponse>>(`/admin/users/${uuid}`),

  approveUser: (uuid: string) =>
    springApi.post<ApiResponse<void>>(`/admin/users/${uuid}/approve`),

  rejectUser: (uuid: string, reason = 'Identity verification failed') =>
    springApi.post<ApiResponse<void>>(`/admin/users/${uuid}/reject`, null, {
      params: { reason },
    }),

  lockUser: (uuid: string, reason = 'Suspicious activity') =>
    springApi.post<ApiResponse<void>>(`/admin/users/${uuid}/lock`, null, {
      params: { reason },
    }),

  unlockUser: (uuid: string) =>
    springApi.post<ApiResponse<void>>(`/admin/users/${uuid}/unlock`),

  resetPassword: (uuid: string) =>
    springApi.post<ApiResponse<string>>(`/admin/users/${uuid}/reset-password`),

  assignRole: (uuid: string, role: UserRole) =>
    springApi.put<ApiResponse<void>>(`/admin/users/${uuid}/assign-role`, null, {
      params: { role },
    }),

  deleteUser: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/users/${uuid}`),

  createUser: (data: any) =>
    springApi.post<ApiResponse<UserResponse>>(`/admin/users`, data),
}
