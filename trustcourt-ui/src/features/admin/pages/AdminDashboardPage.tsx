// ─── AdminDashboardPage — Phase F10: Live Backend Integration ─────────────────
import { useNavigate } from 'react-router-dom'
import { PageHeader, ContentContainer, Section, CardSection, GridLayout } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { LoadingLayout, ErrorLayout } from '@/shared/components/layout/StateLayouts'
import { ProgressBadge } from '@/shared/components/badges'
import { useAdminDashboard } from '../hooks/useAdminDashboard'
import { ROUTES } from '@/constants/routes'

function StatCard({ title, value, sub, color = '#111827' }: {
  title: string; value: string | number; sub?: string; color?: string
}) {
  return (
    <CardSection title={title} className="text-center">
      <div style={{ fontSize: '2rem', fontWeight: 700, color, lineHeight: 1.2 }}>
        {typeof value === 'number' ? value.toLocaleString() : value}
      </div>
      {sub && <div style={{ fontSize: '0.8125rem', color: '#6B7280', marginTop: '0.375rem' }}>{sub}</div>}
    </CardSection>
  )
}

export function AdminDashboardPage() {
  const navigate  = useNavigate()
  const { data, isLoading, isError, error, refetch } = useAdminDashboard()

  if (isLoading) return <LoadingLayout message="Loading dashboard…" />
  if (isError)   return <ErrorLayout message={(error as Error)?.message ?? 'Failed to load dashboard'} action={<Button onClick={() => refetch()}>Retry</Button>} />
  if (!data)     return null

  const aiStatus = data.aiEnabled ? '#059669' : '#DC2626'
  const aiLabel  = data.aiEnabled ? 'AI Enabled' : 'AI Disabled'

  return (
    <ContentContainer maxWidth="100%">
      <PageHeader
        title="Admin Control Center"
        subtitle="Platform overview, system health, and operational metrics. Auto-refreshes every 30 seconds."
        actions={
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <Button variant="ghost" onClick={() => refetch()}>↻ Refresh</Button>
            <Button onClick={() => navigate(ROUTES.ADMIN.REPORTS)}>Generate Report</Button>
          </div>
        }
      />

      {/* Row 1 – User Stats */}
      <GridLayout cols={4}>
        <StatCard title="Total Users" value={data.totalUsers} sub={`${data.activeUsers.toLocaleString()} active`} />
        <StatCard title="Pending Approvals" value={data.pendingApprovals}
          sub={data.pendingApprovals > 0 ? 'Requires attention' : 'None pending'}
          color={data.pendingApprovals > 0 ? '#D97706' : '#059669'} />
        <StatCard title="Locked Accounts" value={data.lockedUsers}
          sub={data.lockedUsers > 0 ? 'Locked accounts' : 'None locked'}
          color={data.lockedUsers > 0 ? '#DC2626' : '#059669'} />
        <StatCard title="System Health"
          value={data.activeMaintenanceWindows > 0 ? 'Maintenance' : '99.9%'}
          sub={data.activeMaintenanceWindows > 0 ? `${data.activeMaintenanceWindows} active window(s)` : 'All services operational'}
          color={data.activeMaintenanceWindows > 0 ? '#D97706' : '#059669'} />
      </GridLayout>

      {/* Row 2 – Court + Case + AI */}
      <GridLayout cols={4} className="mt-6">
        <StatCard title="Total Courts" value={data.totalCourts} sub={`${data.totalBenches} benches · ${data.totalCourtRooms} rooms`} />
        <StatCard title="Registered Cases" value={data.registeredCases} sub={`${data.pendingCases.toLocaleString()} pending`} />
        <StatCard title="Today's Hearings" value={data.todayHearings} sub={`${data.disposedCases.toLocaleString()} disposed total`} />
        <StatCard title="AI Analysis Today" value={data.aiAnalyzedCasesToday} sub={aiLabel} color={aiStatus} />
      </GridLayout>

      {/* Row 3 – Alerts + Registrations + AI */}
      <GridLayout cols={3} className="mt-6">
        <Section title="Security Alerts">
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {data.highSeveritySecurityEvents > 0 && (
              <div style={{ padding: '0.75rem', background: '#FEF2F2', border: '1px solid #FCA5A5', borderRadius: '0.375rem' }}>
                <div style={{ fontSize: '0.875rem', fontWeight: 600, color: '#991B1B' }}>High Severity Events</div>
                <div style={{ fontSize: '0.8125rem', color: '#B91C1C', marginTop: 2 }}>{data.highSeveritySecurityEvents} event(s) today</div>
              </div>
            )}
            {data.failedLoginsToday > 0 && (
              <div style={{ padding: '0.75rem', background: '#FFFBEB', border: '1px solid #FDE68A', borderRadius: '0.375rem' }}>
                <div style={{ fontSize: '0.875rem', fontWeight: 600, color: '#92400E' }}>Failed Login Attempts</div>
                <div style={{ fontSize: '0.8125rem', color: '#B45309', marginTop: 2 }}>{data.failedLoginsToday} failed attempt(s) today</div>
              </div>
            )}
            {data.highSeveritySecurityEvents === 0 && data.failedLoginsToday === 0 && (
              <div style={{ padding: '0.75rem', background: '#F0FDF4', border: '1px solid #BBF7D0', borderRadius: '0.375rem' }}>
                <div style={{ fontSize: '0.875rem', fontWeight: 600, color: '#166534' }}>No Critical Alerts</div>
                <div style={{ fontSize: '0.8125rem', color: '#15803D', marginTop: 2 }}>Platform is operating normally</div>
              </div>
            )}
            <div style={{ fontSize: '0.8125rem', color: '#6B7280' }}>
              Active sessions: <strong style={{ color: '#111827' }}>{data.activeSessions}</strong>
            </div>
          </div>
        </Section>

        <Section title="Recent Pending Approvals">
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {data.recentPendingUsers?.length === 0 && (
              <p style={{ fontSize: '0.875rem', color: '#9CA3AF' }}>No pending approvals.</p>
            )}
            {data.recentPendingUsers?.slice(0, 5).map((u) => (
              <div key={u.uuid} style={{
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                padding: '0.625rem 0.75rem', background: '#FFFFFF',
                border: '1px solid #E5E7EB', borderRadius: '0.375rem'
              }}>
                <div>
                  <div style={{ fontSize: '0.875rem', fontWeight: 600, color: '#111827' }}>{u.fullName}</div>
                  <div style={{ fontSize: '0.75rem', color: '#6B7280' }}>{u.email} · {u.role}</div>
                </div>
                <StatusBadge status="pending" label="Pending" />
              </div>
            ))}
            {(data.pendingApprovals ?? 0) > 5 && (
              <button
                onClick={() => navigate(ROUTES.ADMIN.USERS)}
                style={{ fontSize: '0.8125rem', color: '#0F1D3A', fontWeight: 600, background: 'none', border: 'none', cursor: 'pointer', textAlign: 'left', padding: 0 }}
              >
                View all {data.pendingApprovals} pending →
              </button>
            )}
          </div>
        </Section>

        <Section title="AI Engine Status">
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.375rem' }}>
                <span>Platform AI</span>
                <span style={{ color: aiStatus }}>{aiLabel}</span>
              </div>
              <ProgressBadge value={data.aiEnabled ? 100 : 0} showLabel={false} className="w-full" />
            </div>
            <div style={{ fontSize: '0.8125rem', color: '#6B7280', display: 'flex', flexDirection: 'column', gap: '0.375rem' }}>
              <div>Model: <strong style={{ color: '#111827' }}>{data.aiModelVersion || 'N/A'}</strong></div>
              <div>Cases analysed today: <strong style={{ color: '#111827' }}>{data.aiAnalyzedCasesToday}</strong></div>
              <div>Audit events today: <strong style={{ color: '#111827' }}>{data.auditEventsToday}</strong></div>
            </div>
          </div>
        </Section>
      </GridLayout>

      {/* Row 4 – Court Activity + Stats */}
      <GridLayout cols={2} className="mt-6">
        <Section title="Court Activity (Today)">
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.9375rem', color: '#4B5563' }}>
            {[
              ['Total Courts', data.totalCourts],
              ['Total Benches', data.totalBenches],
              ['Court Rooms', data.totalCourtRooms],
              ['Hearings Scheduled', data.todayHearings],
              ['Active Announcements', data.activeAnnouncements],
            ].map(([label, val]) => (
              <div key={String(label)} style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #F3F4F6', paddingBottom: '0.375rem' }}>
                <span>{label}</span>
                <span style={{ fontWeight: 600, color: '#111827' }}>{Number(val).toLocaleString()}</span>
              </div>
            ))}
          </div>
        </Section>

        <Section title="Role Distribution">
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.625rem' }}>
            {[
              { label: 'Judges', value: data.totalJudges, color: '#3B82F6' },
              { label: 'Advocates', value: data.totalAdvocates, color: '#8B5CF6' },
              { label: 'Clerks', value: data.totalClerks, color: '#F59E0B' },
              { label: 'Admins', value: data.totalAdmins, color: '#10B981' },
            ].map(({ label, value, color }) => {
              const pct = data.totalUsers > 0 ? Math.round((value / data.totalUsers) * 100) : 0
              return (
                <div key={label}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8125rem', fontWeight: 500, marginBottom: '0.25rem' }}>
                    <span style={{ color: '#374151' }}>{label}</span>
                    <span style={{ color: '#6B7280' }}>{value.toLocaleString()} ({pct}%)</span>
                  </div>
                  <div style={{ height: 6, background: '#E5E7EB', borderRadius: 3, overflow: 'hidden' }}>
                    <div style={{ height: '100%', width: `${pct}%`, background: color, borderRadius: 3, transition: 'width 0.5s ease' }} />
                  </div>
                </div>
              )
            })}
          </div>
        </Section>
      </GridLayout>
    </ContentContainer>
  )
}
