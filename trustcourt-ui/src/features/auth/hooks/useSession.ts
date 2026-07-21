// ─── useSession Hook ──────────────────────────────────────────────────────────
// Phase F2 – Session state and idle management
import { useSessionContext } from '@/features/auth/contexts/SessionContext'

export function useSession() {
  return useSessionContext()
}
