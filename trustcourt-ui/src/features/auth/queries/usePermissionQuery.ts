// ─── usePermissionQuery ───────────────────────────────────────────────────────
// Phase F2 – Locally resolves permissions from ROLE_REGISTRY and writes to permissionSlice.
// No API call — permissions are role-derived in Phase F2.
import { useEffect }            from 'react'
import { useAppDispatch, useAppSelector } from '@/store'
import { selectUserRole }        from '@/store/slices/authSlice'
import { selectResolvedPermissions } from '@/store/slices/permissionSlice'
import { permissionService }     from '@/features/auth/services/permissionService'

export function usePermissionQuery() {
  const dispatch    = useAppDispatch()
  const role        = useAppSelector(selectUserRole)
  const permissions = useAppSelector(selectResolvedPermissions)

  useEffect(() => {
    permissionService.init(dispatch)
    // Resolve permissions if role is set but permissions not yet loaded
    if (role && permissions.length === 0) {
      permissionService.resolvePermissions(role)
    }
  }, [dispatch, role, permissions.length])

  return { permissions, role }
}
