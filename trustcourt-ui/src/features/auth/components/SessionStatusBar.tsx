// ─── Session Status Bar ───────────────────────────────────────────────────────
// Phase F2 – Small developer/debug bar showing session metadata
// Only renders in development (import.meta.env.DEV)
import { useSession }       from '@/features/auth/hooks/useSession'
import { useToken }         from '@/features/auth/hooks/useToken'
import { useRole }          from '@/features/auth/hooks/useRole'

export function SessionStatusBar() {
  if (!import.meta.env.DEV) return null

  const { metadata, lastActivity }     = useSession()
  const { isExpired, remainingMs }     = useToken()
  const { roleLabel }            = useRole()

  const remainingMin = remainingMs ? Math.ceil(remainingMs / 60000) : null
  const idleMinutes  = Math.floor((Date.now() - lastActivity) / 60000)

  return (
    <div
      title="Dev Session Status — Hidden in production"
      style={{
        position:   'fixed',
        bottom:     0,
        left:       0,
        right:      0,
        padding:    '4px 16px',
        background: 'rgba(0,0,0,0.85)',
        color:      '#64748B',
        fontSize:   11,
        fontFamily: 'monospace',
        display:    'flex',
        gap:        16,
        alignItems: 'center',
        zIndex:     8000,
      }}
    >
      <span style={{ color: '#22C55E' }}>⬤ DEV</span>
      <span>Role: <strong style={{ color: '#818CF8' }}>{roleLabel}</strong></span>
      <span>Token: <strong style={{ color: isExpired ? '#EF4444' : '#22C55E' }}>{isExpired ? 'EXPIRED' : `${remainingMin}m`}</strong></span>
      <span>Idle: <strong style={{ color: '#F59E0B' }}>{idleMinutes}m</strong></span>
      {metadata && <span>Browser: <strong style={{ color: '#94A3B8' }}>{metadata.browserName}/{metadata.deviceType}</strong></span>}
    </div>
  )
}
