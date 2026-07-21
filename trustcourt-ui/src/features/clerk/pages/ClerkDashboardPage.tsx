// ─── ClerkDashboardPage — Phase F8 ────────────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { ROUTES } from '@/constants/routes'
import { Link } from 'react-router-dom'

export function ClerkDashboardPage() {
  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Clerk Overview" 
        description="Monitor today's scrutiny workload, case registrations, and upcoming hearings."
      />

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '1.5rem' }}>
        <div style={{ background: '#FFF', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <span style={{ fontSize: '0.875rem', color: '#6B7280', fontWeight: 500 }}>Pending Scrutiny</span>
          <span style={{ fontSize: '2.25rem', fontWeight: 700, color: '#0F1D3A', marginTop: '0.5rem' }}>24</span>
        </div>
        <div style={{ background: '#FFF', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <span style={{ fontSize: '0.875rem', color: '#6B7280', fontWeight: 500 }}>Cases Registered (Today)</span>
          <span style={{ fontSize: '2.25rem', fontWeight: 700, color: '#10B981', marginTop: '0.5rem' }}>18</span>
        </div>
        <div style={{ background: '#FFF', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <span style={{ fontSize: '0.875rem', color: '#6B7280', fontWeight: 500 }}>Returned Cases</span>
          <span style={{ fontSize: '2.25rem', fontWeight: 700, color: '#F59E0B', marginTop: '0.5rem' }}>5</span>
        </div>
        <div style={{ background: '#FFF', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <span style={{ fontSize: '0.875rem', color: '#6B7280', fontWeight: 500 }}>Rejected</span>
          <span style={{ fontSize: '2.25rem', fontWeight: 700, color: '#EF4444', marginTop: '0.5rem' }}>2</span>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '2rem' }}>
        <div style={{ background: '#FFF', borderRadius: '12px', border: '1px solid #E5E7EB', padding: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <h3 style={{ margin: 0, fontSize: '1.125rem' }}>Priority Scrutiny Queue</h3>
            <Link to={ROUTES.CLERK.SUBMISSIONS} style={{ fontSize: '0.875rem', color: '#2563EB', textDecoration: 'none', fontWeight: 500 }}>View All Submissions →</Link>
          </div>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {[1, 2, 3].map(i => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '1rem', border: '1px solid #E5E7EB', borderRadius: '8px' }}>
                <div>
                  <div style={{ fontWeight: 600, color: '#111827' }}>Sub-2026-00{i} <span style={{ color: '#6B7280', fontWeight: 400, marginLeft: '0.5rem' }}>Civil Suit</span></div>
                  <div style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.25rem' }}>Submitted 2 hours ago</div>
                </div>
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                  <StatusBadge status={i === 1 ? 'High Priority' : 'Standard'} />
                  <Link to={ROUTES.CLERK.SCRUTINY.replace(':id', `Sub-2026-00${i}`)} style={{ padding: '0.5rem 1rem', background: '#F3F4F6', color: '#111827', borderRadius: '6px', fontSize: '0.875rem', fontWeight: 500, textDecoration: 'none' }}>Begin Scrutiny</Link>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <div style={{ background: '#FFF', borderRadius: '12px', border: '1px solid #E5E7EB', padding: '1.5rem' }}>
            <h3 style={{ margin: '0 0 1.5rem 0', fontSize: '1.125rem' }}>Quick Actions</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <Link to={ROUTES.CLERK.SUBMISSIONS} style={{ padding: '0.75rem 1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 500, color: '#111827', textDecoration: 'none', display: 'block' }}>Review Submissions</Link>
              <Link to={ROUTES.CLERK.HEARINGS} style={{ padding: '0.75rem 1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 500, color: '#111827', textDecoration: 'none', display: 'block' }}>Schedule Hearings</Link>
              <Link to={ROUTES.CLERK.RETURNED} style={{ padding: '0.75rem 1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 500, color: '#111827', textDecoration: 'none', display: 'block' }}>Process Returned Cases</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
