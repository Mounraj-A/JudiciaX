// ─── Admin Announcement Hooks ─────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminAnnouncementApi, type AnnouncementRequest } from '../api/adminAnnouncementApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminAnnouncements(page = 0, size = 20) {
  return useQuery({
    queryKey: queryKeys.announcements.all(),
    queryFn:  () => adminAnnouncementApi.listAnnouncements(page, size),
    select:   (res) => res.data,
  })
}

export function useCreateAnnouncement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: AnnouncementRequest) => adminAnnouncementApi.createAnnouncement(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.announcements.all() }),
  })
}

export function useUpdateAnnouncement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: AnnouncementRequest }) =>
      adminAnnouncementApi.updateAnnouncement(uuid, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.announcements.all() }),
  })
}

export function useDeleteAnnouncement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminAnnouncementApi.deleteAnnouncement(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.announcements.all() }),
  })
}

export function usePublishAnnouncement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminAnnouncementApi.publishAnnouncement(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.announcements.all() }),
  })
}

export function useArchiveAnnouncement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminAnnouncementApi.archiveAnnouncement(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.announcements.all() }),
  })
}
