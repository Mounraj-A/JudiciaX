// ─── Admin Config Hooks ───────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminConfigApi, type ConfigurationRequest } from '../api/adminConfigApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminConfigurations() {
  return useQuery({
    queryKey: queryKeys.config.all(),
    queryFn:  () => adminConfigApi.listConfigurations(),
    select:   (res) => res.data,
  })
}

export function useConfigByKey(key: string) {
  return useQuery({
    queryKey: queryKeys.config.byKey(key),
    queryFn:  () => adminConfigApi.getConfigByKey(key),
    enabled:  !!key,
    select:   (res) => res.data,
  })
}

export function useCreateConfiguration() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: ConfigurationRequest) => adminConfigApi.createConfiguration(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.config.all() }),
  })
}

export function useUpdateConfiguration() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: ConfigurationRequest }) =>
      adminConfigApi.updateConfiguration(uuid, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.config.all() }),
  })
}

export function useDeleteConfiguration() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminConfigApi.deleteConfiguration(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.config.all() }),
  })
}
