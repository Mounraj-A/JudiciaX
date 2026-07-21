// ─── Auth Loading State ───────────────────────────────────────────────────────
// Phase F2 – Full-screen branded loading state during app initialization
// Phase F2 – Full-screen branded loading state during app initialization

/**
 * Shown during session recovery / app initialization.
 * Accessible: role="status" + aria-label.
 */
export function AuthLoadingState({ message }: { message?: string }) {
  return (
    <div
      role="status"
      aria-label="Loading TrustCourt"
      style={{
        position:       'fixed',
        inset:          0,
        display:        'flex',
        flexDirection:  'column',
        alignItems:     'center',
        justifyContent: 'center',
        background:     'linear-gradient(135deg, #0A0F1E 0%, #0D1B2A 50%, #0A1628 100%)',
        zIndex:         9999,
      }}
    >
      {/* Logo */}
      <div style={{ marginBottom: 32, textAlign: 'center' }}>
        <div style={{
          width: 72, height: 72, borderRadius: 20,
          background: 'linear-gradient(135deg, #1E40AF, #7C3AED)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          margin: '0 auto 16px',
          boxShadow: '0 0 40px rgba(124,58,237,0.4)',
        }}>
          <span style={{ fontSize: 36 }}>⚖</span>
        </div>
        <h1 style={{ color: '#F8FAFC', fontSize: 24, fontWeight: 700, margin: 0, letterSpacing: '-0.5px' }}>
          TrustCourt
        </h1>
        <p style={{ color: '#64748B', fontSize: 12, margin: '4px 0 0', letterSpacing: 2, textTransform: 'uppercase' }}>
          Enterprise Judicial Platform
        </p>
      </div>

      {/* Spinner */}
      <div style={{ position: 'relative', width: 48, height: 48 }}>
        <div style={{
          position: 'absolute', inset: 0,
          border: '3px solid rgba(124,58,237,0.2)',
          borderTopColor: '#7C3AED',
          borderRadius: '50%',
          animation: 'spin 0.8s linear infinite',
        }} />
        <style>{`@keyframes spin { to { transform: rotate(360deg) } }`}</style>
      </div>

      {/* Message */}
      <p style={{ color: '#64748B', fontSize: 13, marginTop: 24, textAlign: 'center' }}>
        {message ?? 'Securing your session...'}
      </p>
    </div>
  )
}
