// ─── Admin Announcement API ───────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface AnnouncementResponse {
  uuid: string
  title: string
  content: string
  targetRoles: string[]
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  publishedAt?: string
  expiresAt?: string
  createdByUuid: string
  createdAt: string
}

export interface AnnouncementRequest {
  title: string
  content: string
  targetRoles: string[]
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  expiresAt?: string
}

export const adminAnnouncementApi = {
  listAnnouncements: (page = 0, size = 20) =>
    springApi.get<ApiResponse<{ content: AnnouncementResponse[]; totalElements: number }>>('/admin/announcements', {
      params: { page, size },
    }),

  getAnnouncementByUuid: (uuid: string) =>
    springApi.get<ApiResponse<AnnouncementResponse>>(`/admin/announcements/${uuid}`),

  createAnnouncement: (data: AnnouncementRequest) =>
    springApi.post<ApiResponse<AnnouncementResponse>>('/admin/announcements', data),

  updateAnnouncement: (uuid: string, data: AnnouncementRequest) =>
    springApi.put<ApiResponse<AnnouncementResponse>>(`/admin/announcements/${uuid}`, data),

  deleteAnnouncement: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/announcements/${uuid}`),

  publishAnnouncement: (uuid: string) =>
    springApi.put<ApiResponse<void>>(`/admin/announcements/${uuid}/publish`),

  archiveAnnouncement: (uuid: string) =>
    springApi.put<ApiResponse<void>>(`/admin/announcements/${uuid}/archive`),
}
