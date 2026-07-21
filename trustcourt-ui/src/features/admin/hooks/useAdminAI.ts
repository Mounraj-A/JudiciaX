// ─── Admin AI Hooks ───────────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminAiApi } from '../api/adminAiApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminAISettings() {
  return useQuery({
    queryKey: queryKeys.adminAi.settings(),
    queryFn:  () => adminAiApi.getAISettings(),
    select:   (res) => res.data,
  })
}

export function useAdminAIUsage() {
  return useQuery({
    queryKey: queryKeys.adminAi.usage(),
    queryFn:  () => adminAiApi.getAIUsage(),
    refetchInterval: 60_000,
    select:   (res) => res.data,
  })
}

export function useAIGatewayHealth() {
  return useQuery({
    queryKey: queryKeys.adminAi.health(),
    queryFn:  () => adminAiApi.getAIGatewayHealth(),
    refetchInterval: 30_000,
  })
}

export function useOCRQueue() {
  return useQuery({
    queryKey: queryKeys.adminAi.ocrQueue(),
    queryFn:  () => adminAiApi.getOCRQueue(),
    refetchInterval: 15_000,
    select:   (res) => res.data,
  })
}

export function useAIAnalytics() {
  return useQuery({
    queryKey: queryKeys.adminAi.analytics(),
    queryFn:  () => adminAiApi.getAIAnalytics(),
    select:   (res) => res.data,
  })
}

export function useToggleAI() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (enable: boolean) => enable ? adminAiApi.enableAI() : adminAiApi.disableAI(),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.adminAi.settings() }),
  })
}

export function useSetModelVersion() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (version: string) => adminAiApi.setModelVersion(version),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.adminAi.settings() }),
  })
}

export function useSetPriorityThreshold() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (threshold: number) => adminAiApi.setPriorityThreshold(threshold),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.adminAi.settings() }),
  })
}

export function useSetConfidenceThreshold() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (threshold: number) => adminAiApi.setConfidenceThreshold(threshold),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.adminAi.settings() }),
  })
}

export function useSetExplainability() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (enabled: boolean) => adminAiApi.setExplainability(enabled),
    onSuccess: () => qc.invalidateQueries({ queryKey: queryKeys.adminAi.settings() }),
  })
}
