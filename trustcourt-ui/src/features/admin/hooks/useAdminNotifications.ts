// ─── Admin Notification Hooks ─────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminNotificationApi, type NotificationTemplateRequest, type NotificationSearchRequest } from '../api/adminNotificationApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminNotifications(page = 0, size = 20) {
  return useQuery({
    queryKey: queryKeys.notifications.all(),
    queryFn:  () => adminNotificationApi.listNotifications(page, size),
    select:   (res) => res.data,
    refetchInterval: 60_000,
  })
}

export function useUnreadNotificationCount() {
  return useQuery({
    queryKey: queryKeys.notifications.unread(),
    queryFn:  () => adminNotificationApi.getUnreadCount(),
    refetchInterval: 30_000,
    select:   (res) => res.data,
  })
}

export function useMarkAllNotificationsRead() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: () => adminNotificationApi.markAllRead(),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notifications'] }),
  })
}

export function useDeleteNotification() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminNotificationApi.deleteNotification(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notifications'] }),
  })
}

export function useSearchNotifications(body: NotificationSearchRequest, enabled = true) {
  return useQuery({
    queryKey: queryKeys.notifications.search(body),
    queryFn:  () => adminNotificationApi.searchNotifications(body),
    enabled,
    select:   (res) => res.data,
  })
}

// Templates
export function useNotificationTemplates() {
  return useQuery({
    queryKey: queryKeys.notifications.templates(),
    queryFn:  () => adminNotificationApi.listTemplates(),
    select:   (res) => res.data,
  })
}

export function useCreateNotificationTemplate() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: NotificationTemplateRequest) => adminNotificationApi.createTemplate(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.notifications.templates() }),
  })
}

export function useUpdateNotificationTemplate() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: NotificationTemplateRequest }) =>
      adminNotificationApi.updateTemplate(uuid, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.notifications.templates() }),
  })
}

export function useDeleteNotificationTemplate() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminNotificationApi.deleteTemplate(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.notifications.templates() }),
  })
}

export function useRetryFailedNotificationEvents() {
  return useMutation({
    mutationFn: () => adminNotificationApi.retryFailedEvents(),
  })
}
