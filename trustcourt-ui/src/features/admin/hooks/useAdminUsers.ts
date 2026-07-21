// ─── Admin User Hooks ─────────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminUserApi, type UserRole } from '../api/adminUserApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminUsers(page = 0, size = 20, role?: string) {
  return useQuery({
    queryKey: queryKeys.users.all({ page, size, role }),
    queryFn:  () => adminUserApi.listUsers(page, size, role),
    select:   (res) => res.data,
  })
}

export function useAdminUser(uuid: string) {
  return useQuery({
    queryKey: queryKeys.users.byId(uuid),
    queryFn:  () => adminUserApi.getUserByUuid(uuid),
    enabled:  !!uuid,
    select:   (res) => res.data,
  })
}

export function useApproveUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminUserApi.approveUser(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
  })
}

export function useRejectUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, reason }: { uuid: string; reason?: string }) =>
      adminUserApi.rejectUser(uuid, reason),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
  })
}

export function useLockUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, reason }: { uuid: string; reason?: string }) =>
      adminUserApi.lockUser(uuid, reason),
    onSuccess: (_, { uuid }) => {
      qc.setQueriesData({ queryKey: ['users'] }, (old: any) => {
        if (!old?.content) return old
        return { ...old, content: old.content.map((u: any) => u.uuid === uuid ? { ...u, isLocked: true, accountStatus: 'LOCKED' } : u) }
      })
      qc.invalidateQueries({ queryKey: ['users'] })
    },
  })
}

export function useUnlockUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminUserApi.unlockUser(uuid),
    onSuccess: (_, uuid) => {
      qc.setQueriesData({ queryKey: ['users'] }, (old: any) => {
        if (!old?.content) return old
        return { ...old, content: old.content.map((u: any) => u.uuid === uuid ? { ...u, isLocked: false, accountStatus: 'ACTIVE' } : u) }
      })
      qc.invalidateQueries({ queryKey: ['users'] })
    },
  })
}

export function useResetUserPassword() {
  return useMutation({
    mutationFn: (uuid: string) => adminUserApi.resetPassword(uuid),
  })
}

export function useAssignRole() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, role }: { uuid: string; role: UserRole }) =>
      adminUserApi.assignRole(uuid, role),
    onSuccess: (_, { uuid, role }) => {
      qc.setQueriesData({ queryKey: ['users'] }, (old: any) => {
        if (!old?.content) return old
        return { ...old, content: old.content.map((u: any) => u.uuid === uuid ? { ...u, role } : u) }
      })
      qc.invalidateQueries({ queryKey: ['users'] })
    },
  })
}

export function useDeleteUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminUserApi.deleteUser(uuid),
    onSuccess: (_, uuid) => {
      qc.setQueriesData({ queryKey: ['users'] }, (old: any) => {
        if (!old?.content) return old
        return { ...old, content: old.content.map((u: any) => u.uuid === uuid ? { ...u, isDeleted: true } : u) }
      })
      qc.invalidateQueries({ queryKey: ['users'] })
    },
  })
}

export function useCreateUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: any) => adminUserApi.createUser(data),
    onSuccess: (res: any) => {
      qc.setQueriesData({ queryKey: ['users'] }, (old: any) => {
        if (!old?.content) return old
        return { ...old, content: [res.data, ...old.content] }
      })
      qc.invalidateQueries({ queryKey: ['users'] })
    },
  })
}
