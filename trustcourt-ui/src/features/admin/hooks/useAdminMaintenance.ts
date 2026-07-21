// ─── Admin Maintenance Hooks ──────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminMaintenanceApi, type MaintenanceRequest } from '../api/adminMaintenanceApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminMaintenance() {
  return useQuery({
    queryKey: queryKeys.maintenance.all(),
    queryFn:  () => adminMaintenanceApi.listMaintenance(),
    select:   (res) => res.data,
  })
}

export function useCreateMaintenance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: MaintenanceRequest) => adminMaintenanceApi.createMaintenance(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.maintenance.all() }),
  })
}

export function useUpdateMaintenance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: MaintenanceRequest }) =>
      adminMaintenanceApi.updateMaintenance(uuid, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.maintenance.all() }),
  })
}

export function useDeleteMaintenance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminMaintenanceApi.deleteMaintenance(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.maintenance.all() }),
  })
}

export function useActivateMaintenance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminMaintenanceApi.activateMaintenance(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.maintenance.all() }),
  })
}

export function useCompleteMaintenance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminMaintenanceApi.completeMaintenance(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.maintenance.all() }),
  })
}

export function useCancelMaintenance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminMaintenanceApi.cancelMaintenance(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.maintenance.all() }),
  })
}
