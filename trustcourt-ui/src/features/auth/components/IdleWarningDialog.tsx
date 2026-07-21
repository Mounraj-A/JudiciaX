// ─── Idle Warning Dialog ──────────────────────────────────────────────────────
// Phase F2 – Shown when idle timer is 2 minutes from auto-logout
// Integrates with SessionContext and FeatureGuard.IDLE_WARNING
import { useSession }  from '@/features/auth/hooks/useSession'
import { useAuth }     from '@/features/auth/hooks/useAuth'
import { FeatureGuard } from '@/core/guards/FeatureGuard'

export function IdleWarningDialog() {
  const { idleWarning, extendSession } = useSession()
  const { logout }                     = useAuth()

  if (!idleWarning) return null

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="idle-warning-title"
      aria-describedby="idle-warning-desc"
      style={{
        position:       'fixed',
        inset:          0,
        background:     'rgba(0,0,0,0.7)',
        backdropFilter: 'blur(8px)',
        display:        'flex',
        alignItems:     'center',
        justifyContent: 'center',
        zIndex:         10000,
        fontFamily:     'Inter, sans-serif',
      }}
    >
      <div style={{
        width:          400,
        background:     'rgba(13,27,42,0.95)',
        border:         '1px solid rgba(255,255,255,0.1)',
        borderRadius:   20,
        padding:        40,
        boxShadow:      '0 32px 64px rgba(0,0,0,0.6)',
        textAlign:      'center',
      }}>
        {/* Icon */}
        <div style={{
          width:          64,
          height:         64,
          background:     'rgba(245,158,11,0.15)',
          border:         '1px solid rgba(245,158,11,0.3)',
          borderRadius:   '50%',
          display:        'flex',
          alignItems:     'center',
          justifyContent: 'center',
          margin:         '0 auto 20px',
          fontSize:       28,
        }}>
          ⏳
        </div>

        <h2
          id="idle-warning-title"
          style={{ color: '#F1F5F9', fontSize: 20, fontWeight: 700, margin: '0 0 12px' }}
        >
          Still there?
        </h2>
        <p
          id="idle-warning-desc"
          style={{ color: '#94A3B8', fontSize: 14, margin: '0 0 32px', lineHeight: 1.6 }}
        >
          You've been idle for a while. For security, your session will end in&nbsp;
          <strong style={{ color: '#FCD34D' }}>2 minutes</strong> unless you continue.
        </p>

        {/* Actions */}
        <div style={{ display: 'flex', gap: 12, justifyContent: 'center' }}>
          <button
            onClick={extendSession}
            autoFocus
            style={{
              padding:       '12px 28px',
              background:    'linear-gradient(135deg, #1E40AF, #7C3AED)',
              border:        'none',
              borderRadius:  10,
              color:         '#fff',
              fontSize:      14,
              fontWeight:    600,
              cursor:        'pointer',
              boxShadow:     '0 4px 16px rgba(124,58,237,0.4)',
            }}
          >
            Continue Session
          </button>
          <button
            onClick={() => void logout()}
            style={{
              padding:       '12px 28px',
              background:    'rgba(255,255,255,0.06)',
              border:        '1px solid rgba(255,255,255,0.1)',
              borderRadius:  10,
              color:         '#64748B',
              fontSize:      14,
              cursor:        'pointer',
            }}
          >
            Sign Out
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── Guarded export ───────────────────────────────────────────────────────────
export function IdleWarningDialogGuarded() {
  return (
    <FeatureGuard feature="IDLE_WARNING">
      <IdleWarningDialog />
    </FeatureGuard>
  )
}
