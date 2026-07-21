import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getNotifications, markAsRead, markAllRead } from '@/api/services/advocate/notificationApi'

export const ADVOCATE_NOTIFICATIONS_QUERY_KEY = ['advocate-notifications']

export const useAdvocateNotifications = (page: number, size: number, unreadOnly: boolean) => {
  return useQuery({
    queryKey: [...ADVOCATE_NOTIFICATIONS_QUERY_KEY, 'list', page, size, unreadOnly],
    queryFn: () => getNotifications(page, size, unreadOnly)
  })
}

export const useMarkNotificationRead = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => markAsRead(uuid),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADVOCATE_NOTIFICATIONS_QUERY_KEY })
    }
  })
}

export const useMarkAllNotificationsRead = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => markAllRead(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADVOCATE_NOTIFICATIONS_QUERY_KEY })
    }
  })
}
