// ─── Admin Role Hooks ─────────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminRoleApi, type RoleRequest } from '../api/adminRoleApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminRoles() {
  return useQuery({
    queryKey: queryKeys.roles.all(),
    queryFn:  () => adminRoleApi.listRoles(),
    select:   (res) => res.data,
  })
}

export function useAdminRole(uuid: string) {
  return useQuery({
    queryKey: queryKeys.roles.byId(uuid),
    queryFn:  () => adminRoleApi.getRoleByUuid(uuid),
    enabled:  !!uuid,
    select:   (res) => res.data,
  })
}

export function useRolePermissions(uuid: string) {
  return useQuery({
    queryKey: queryKeys.roles.permissions(uuid),
    queryFn:  () => adminRoleApi.getRolePermissions(uuid),
    enabled:  !!uuid,
    select:   (res) => res.data,
  })
}

export function useCreateRole() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: RoleRequest) => adminRoleApi.createRole(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.roles.all() }),
  })
}

export function useUpdateRole() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: RoleRequest }) =>
      adminRoleApi.updateRole(uuid, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['roles'] }),
  })
}

export function useDeleteRole() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminRoleApi.deleteRole(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.roles.all() }),
  })
}

export function useAddPermission() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, permissionCode }: { uuid: string; permissionCode: string }) =>
      adminRoleApi.addPermission(uuid, permissionCode),
    onSuccess: (_d, variables) =>
      qc.invalidateQueries({ queryKey: queryKeys.roles.permissions(variables.uuid) }),
  })
}

export function useRemovePermission() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, permissionCode }: { uuid: string; permissionCode: string }) =>
      adminRoleApi.removePermission(uuid, permissionCode),
    onSuccess: (_d, variables) =>
      qc.invalidateQueries({ queryKey: queryKeys.roles.permissions(variables.uuid) }),
  })
}
