// ─── Admin Notification API ───────────────────────────────────────────────────
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'

export interface NotificationResponse {
  uuid: string
  title: string
  message: string
  type: string
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  isRead: boolean
  sourceEntityType?: string
  sourceEntityUuid?: string
  createdAt: string
}

export interface NotificationTemplateResponse {
  uuid: string
  templateCode: string
  title: string
  body: string
  channel: string
  isActive: boolean
  createdAt: string
}

export interface NotificationTemplateRequest {
  templateCode: string
  title: string
  body: string
  channel: string
  isActive: boolean
}

export interface NotificationSearchRequest {
  isRead?: boolean
  priority?: string
  type?: string
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}

export const adminNotificationApi = {
  listNotifications: (page = 0, size = 20) =>
    springApi.get<ApiResponse<{ content: NotificationResponse[]; totalElements: number }>>('/api/v1/notifications', {
      params: { page, size },
    }),

  getUnreadCount: () =>
    springApi.get<ApiResponse<number>>('/api/v1/notifications/unread-count'),

  markAllRead: () =>
    springApi.put<ApiResponse<void>>('/api/v1/notifications/read-all'),

  deleteNotification: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/api/v1/notifications/${uuid}`),

  searchNotifications: (body: NotificationSearchRequest) =>
    springApi.post<ApiResponse<{ content: NotificationResponse[]; totalElements: number }>>('/api/v1/notifications/search', body),

  // Templates (admin)
  listTemplates: () =>
    springApi.get<ApiResponse<NotificationTemplateResponse[]>>('/api/v1/admin/notification-templates'),

  getTemplate: (uuid: string) =>
    springApi.get<ApiResponse<NotificationTemplateResponse>>(`/api/v1/admin/notification-templates/${uuid}`),

  createTemplate: (data: NotificationTemplateRequest) =>
    springApi.post<ApiResponse<NotificationTemplateResponse>>('/api/v1/admin/notification-templates', data),

  updateTemplate: (uuid: string, data: NotificationTemplateRequest) =>
    springApi.put<ApiResponse<NotificationTemplateResponse>>(`/api/v1/admin/notification-templates/${uuid}`, data),

  deleteTemplate: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/api/v1/admin/notification-templates/${uuid}`),

  retryFailedEvents: () =>
    springApi.post<ApiResponse<void>>('/api/v1/admin/notification-events/retry-failed'),

  getPreferences: () =>
    springApi.get<ApiResponse<Record<string, boolean>>>('/api/v1/notifications/preferences'),

  updatePreferences: (data: Record<string, boolean>) =>
    springApi.put<ApiResponse<void>>('/api/v1/notifications/preferences', data),
}
