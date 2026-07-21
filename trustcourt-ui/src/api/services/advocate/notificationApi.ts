import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse, PaginatedResponse as PageResponse } from '@/types/api'

// ── Types ─────────────────────────────────────────────────────────────

export interface NotificationResponse {
  uuid: string
  title: string
  message: string
  type: string
  isRead: boolean
  createdAt: string
  referenceId?: string
}

// ── API Functions ───────────────────────────────────────────────────────

export const getNotifications = async (page = 0, size = 10, unreadOnly = false): Promise<PageResponse<NotificationResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<NotificationResponse>>>('/advocate/notifications', {
    params: { page, size, unreadOnly }
  })
  return data.data
}

export const markAsRead = async (notificationUuid: string): Promise<void> => {
  await springApiClient.put(`/advocate/notifications/${notificationUuid}/read`)
}

export const markAllRead = async (): Promise<void> => {
  await springApiClient.put('/advocate/notifications/read-all')
}
