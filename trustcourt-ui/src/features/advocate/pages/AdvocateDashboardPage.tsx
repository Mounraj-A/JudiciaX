// ─── Advocate Dashboard — Phase F7 ────────────────────────────────────────────
import { Link } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'
import { PageHeader } from '@/shared/components/layout'
import { EntityAvatar } from '@/shared/components/badges'
import { useAdvocateStats } from '@/features/advocate/api/useAdvocateStats'
import { useAuth } from '@/features/auth/hooks/useAuth'

export function AdvocateDashboardPage() {
  const { data: dashboard, isLoading, error } = useAdvocateStats()
  const { user } = useAuth()

  if (isLoading) {
    return <div style={{ padding: '2rem' }}>Loading dashboard...</div>
  }

  if (error || !dashboard) {
    return <div style={{ padding: '2rem', color: 'red' }}>Failed to load dashboard data. Please try again.</div>
  }

  const stats = [
    { label: 'Active Cases', value: dashboard.activeCases || 0, trend: 'Currently open' },
    { label: 'Pending Drafts', value: dashboard.draftCases || 0, trend: 'Awaiting filing' },
    { label: 'Upcoming Hearings', value: dashboard.upcomingHearings || 0, trend: 'Scheduled' },
    { label: 'Total Filings', value: dashboard.totalCases || 0, trend: 'Lifetime cases' },
  ]

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Advocate Dashboard" 
        description="Manage your filings, track cases, and prepare for upcoming hearings." 
        actions={<Link to={ROUTES.ADVOCATE.NEW_CASE} style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', borderRadius: '8px', textDecoration: 'none', fontSize: '0.875rem', fontWeight: 600 }}>+ New Filing</Link>}
      />

      {/* Welcome Card & Summary */}
      <div style={{ background: '#FFFFFF', borderRadius: '16px', padding: '1.5rem', border: '1px solid #E5E7EB', display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
        <EntityAvatar name={user?.email || 'Advocate'} size={64} />
        <div>
          <h2 style={{ margin: '0 0 0.5rem 0', fontSize: '1.25rem', color: '#111827' }}>Welcome back, {user?.email || 'Counselor'}</h2>
          <p style={{ margin: 0, color: '#6B7280', fontSize: '0.875rem' }}>
            You have {dashboard.upcomingHearings || 0} upcoming hearings.
          </p>
        </div>
      </div>

      {/* Quick Statistics */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '1.5rem' }}>
        {stats.map(s => (
          <div key={s.label} style={{ background: '#FFFFFF', borderRadius: '12px', padding: '1.5rem', border: '1px solid #E5E7EB' }}>
            <div style={{ fontSize: '0.875rem', color: '#6B7280', marginBottom: '0.5rem' }}>{s.label}</div>
            <div style={{ fontSize: '1.875rem', fontWeight: 700, color: '#111827', marginBottom: '0.25rem' }}>{s.value}</div>
            <div style={{ fontSize: '0.75rem', color: '#059669', fontWeight: 500 }}>{s.trend}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '2rem' }}>
        {/* Recent Activity */}
        <div style={{ background: '#FFFFFF', borderRadius: '16px', padding: '1.5rem', border: '1px solid #E5E7EB' }}>
          <h3 style={{ margin: '0 0 1.5rem 0', fontSize: '1rem', color: '#111827' }}>Recent Activity</h3>
          <div style={{ color: '#6B7280', fontSize: '0.875rem', padding: '1rem', background: '#F9FAFB', borderRadius: '8px', textAlign: 'center' }}>
            No recent activity available.
          </div>
        </div>

        {/* Upcoming Hearings */}
        <div style={{ background: '#FFFFFF', borderRadius: '16px', padding: '1.5rem', border: '1px solid #E5E7EB' }}>
          <h3 style={{ margin: '0 0 1.5rem 0', fontSize: '1rem', color: '#111827' }}>Upcoming Hearings</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ color: '#6B7280', fontSize: '0.875rem', padding: '1rem', background: '#F9FAFB', borderRadius: '8px', textAlign: 'center' }}>
               You have {dashboard.upcomingHearings || 0} upcoming hearings. View the Hearings tab for details.
            </div>
          </div>
          <Link to={ROUTES.ADVOCATE.HEARINGS} style={{ display: 'inline-block', marginTop: '1.5rem', fontSize: '0.875rem', color: '#1E40AF', textDecoration: 'none', fontWeight: 500 }}>View all hearings →</Link>
        </div>
      </div>
    </div>
  )
}
export default AdvocateDashboardPage
