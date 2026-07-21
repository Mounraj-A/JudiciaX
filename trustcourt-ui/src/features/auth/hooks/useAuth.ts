// ─── useAuth Hook ─────────────────────────────────────────────────────────────
// Phase F2 – Primary authentication hook
import { useAuthContext }           from '@/features/auth/contexts/AuthContext'
import { useAppSelector }           from '@/store'
import { selectUserRole }           from '@/store/slices/authSlice'

export function useAuth() {
  const ctx  = useAuthContext()
  const role = useAppSelector(selectUserRole)

  return {
    ...ctx,
    role,
    isJudge:      role === 'JUDGE',
    isAdvocate:   role === 'ADVOCATE',
    isClerk:      role === 'CLERK',
    isAdmin:      role === 'ADMIN' || role === 'SUPER_ADMIN',
    isSuperAdmin: role === 'SUPER_ADMIN',
    isAuditor:    role === 'AUDITOR',
    isAiOperator: role === 'AI_OPERATOR',
    isResearcher: role === 'RESEARCHER',
  }
}
