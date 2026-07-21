// â”€â”€â”€ MaintenancePage â€” Phase F5 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

export function MaintenancePage() {
  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: '#0A1628', padding: '2rem', fontFamily: '"Inter", -apple-system, sans-serif'
    }}>
      <div style={{
        maxWidth: 500, width: '100%', background: 'rgba(255,255,255,0.04)', borderRadius: 24,
        border: '1px solid rgba(255,255,255,0.08)', backdropFilter: 'blur(24px)',
        boxShadow: '0 32px 64px rgba(0,0,0,0.5), inset 0 1px 0 rgba(255,255,255,0.06)',
        padding: '3rem 2rem', textAlign: 'center',
        animation: 'scaleIn 0.3s cubic-bezier(0.16, 1, 0.3, 1)'
      }}>
        <div style={{
          width: 80, height: 80, borderRadius: '50%', background: 'linear-gradient(135deg, #1E40AF, #7C3AED)',
          color: '#FFFFFF', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2.5rem',
          margin: '0 auto 1.5rem', boxShadow: '0 0 32px rgba(124,58,237,0.5)'
        }}>
          âš™
        </div>
        <h1 style={{ margin: '0 0 0.75rem 0', fontSize: '1.75rem', fontWeight: 700, color: '#F1F5F9' }}>
          System Maintenance
        </h1>
        <p style={{ margin: '0 0 2rem 0', fontSize: '1rem', color: '#94A3B8', lineHeight: 1.6 }}>
          TrustCourt is currently undergoing scheduled maintenance to improve our infrastructure and security. We apologize for the inconvenience.
        </p>
        
        <div style={{
          background: 'rgba(255,255,255,0.05)', borderRadius: 12, padding: '1.25rem',
          display: 'flex', flexDirection: 'column', gap: '0.5rem', color: '#CBD5E1', fontSize: '0.875rem'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#64748B' }}>Status</span>
            <span style={{ color: '#F59E0B', fontWeight: 600 }}>In Progress</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#64748B' }}>Estimated Completion</span>
            <span>Approx. 2 Hours</span>
          </div>
        </div>
      </div>
      <style>{`@keyframes scaleIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }`}</style>
    </div>
  )
}
export default MaintenancePage
