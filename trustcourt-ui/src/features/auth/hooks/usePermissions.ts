// ─── usePermissions Hook ──────────────────────────────────────────────────────
// Phase F2 – Full permission checking surface
import { useAuthorizationContext } from '@/features/auth/contexts/AuthorizationContext'
import { usePermissionContext }    from '@/features/auth/contexts/PermissionContext'

export function usePermissions() {
  const authorization = useAuthorizationContext()
  const permission    = usePermissionContext()

  return {
    ...authorization,
    ...permission,
  }
}
