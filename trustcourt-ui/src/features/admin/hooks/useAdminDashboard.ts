// ─── Admin Dashboard Hooks ────────────────────────────────────────────────────
import { useQuery } from '@tanstack/react-query'
import { adminDashboardApi } from '../api/adminDashboardApi'
import { queryKeys } from '@/lib/queryClient'

export function useAdminDashboard() {
  return useQuery({
    queryKey: queryKeys.admin.dashboard(),
    queryFn:  () => adminDashboardApi.getDashboard(),
    refetchInterval: 30_000, // auto-refresh every 30s
    select: (res) => res.data,
  })
}
