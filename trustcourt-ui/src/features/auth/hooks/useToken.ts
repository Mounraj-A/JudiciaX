// ─── useToken Hook ────────────────────────────────────────────────────────────
// Phase F2 – Token state + refresh trigger
//
// ⚠️  expiresAt is decoded from JWT for UI DISPLAY ONLY.
//     It is not used for authorization decisions.
import { useAppSelector }        from '@/store'
import { selectIsRefreshing }    from '@/store/slices/authSlice'
import { useAuthContext }        from '@/features/auth/contexts/AuthContext'
import { tokenManager }          from '@/core/session'
import { getTokenExpiryMs }      from '@/core/token/tokenDecoder'
import { isTokenValid }          from '@/core/token/tokenValidator'

export function useToken() {
  const { refresh }   = useAuthContext()
  const isRefreshing  = useAppSelector(selectIsRefreshing)
  const accessToken   = tokenManager.getAccessToken()

  /** Expiry timestamp in ms — for display only. Derived from JWT exp claim. */
  const expiresAt     = getTokenExpiryMs(accessToken)
  const isExpired     = !isTokenValid(accessToken)
  const remainingMs   = expiresAt ? Math.max(0, expiresAt - Date.now()) : null

  return {
    accessToken,
    isExpired,
    isRefreshing,
    expiresAt,       // UI display only — not authoritative
    remainingMs,     // Convenience for "expires in X minutes" display
    refresh,
  }
}
