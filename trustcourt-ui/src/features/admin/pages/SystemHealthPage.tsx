// â”€â”€â”€ SystemHealthPage â€” Phase F10 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { PageHeader, ContentContainer, Section, GridLayout, CardSection } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge } from '@/shared/components/badges'
import { AdminTable } from '../components/AdminTable'
import { useSecurityEvents, useSecuritySummary, useRevokeSession, useRevokeUserSessions } from '../hooks/useAdminSecurity'
import { toast } from 'react-hot-toast'
import { formatDistanceToNow } from 'date-fns'

export function SystemHealthPage() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)

  const { data: summary } = useSecuritySummary()
  const { data: pageData, isLoading, error, refetch } = useSecurityEvents(page, size)

  const revokeSessionMutation = useRevokeSession()
  const revokeUserSessionsMutation = useRevokeUserSessions()

  const handleRevokeSession = async (sessionUuid: string) => {
    try {
      await revokeSessionMutation.mutateAsync(sessionUuid)
      toast.success('Session revoked successfully')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const handleRevokeUserSessions = async (userUuid: string) => {
    try {
      await revokeUserSessionsMutation.mutateAsync(userUuid)
      toast.success('All sessions for user revoked')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const columns = [
    { id: 'time', header: 'Timestamp', renderCell: (_: any, row: any) => formatDistanceToNow(new Date(row.createdAt), { addSuffix: true }) },
    { id: 'type', header: 'Event Type', field: 'eventType' },
    {
      id: 'severity', header: 'Severity',
      renderCell: (_: any, row: any) => {
        const s = row.severity
        const map: any = { LOW: 'info', MEDIUM: 'warning', HIGH: 'danger', CRITICAL: 'danger' }
        return <StatusBadge status={map[s] || 'info'} label={s} />
      }
    },
    { id: 'actor', header: 'Actor', renderCell: (_: any, row: any) => row.actorUsername || 'System' },
    { id: 'ip', header: 'IP Address', field: 'ipAddress' },
    {
      id: 'actions', header: 'Actions', align: 'right',
      renderCell: (_: any, row: any) => (
        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
          {row.actorUuid && (
            <Button variant="ghost" style={{ color: '#D97706' }} onClick={() => handleRevokeUserSessions(row.actorUuid)}>
              Revoke User
            </Button>
          )}
        </div>
      )
    }
  ]

  return (
    <ContentContainer>
      <PageHeader
        title="Security & System Health"
        subtitle="Live monitoring of active sessions, security events, and platform integrity."
        actions={<Button variant="ghost" onClick={() => refetch()}>â†» Refresh</Button>}
      />

      <GridLayout cols={4}>
        <CardSection title="Active Sessions" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#10B981' }}>{summary?.activeSessions || 0}</div>
        </CardSection>
        <CardSection title="Failed Logins (24h)" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#D97706' }}>{summary?.failedLoginsToday || 0}</div>
        </CardSection>
        <CardSection title="Suspicious IPs" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#DC2626' }}>{summary?.suspiciousIps || 0}</div>
        </CardSection>
        <CardSection title="Critical Events (24h)" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#991B1B' }}>{summary?.criticalEventsLast24h || 0}</div>
        </CardSection>
      </GridLayout>

      <Section className="mt-6" title="Recent Security Events">
        <AdminTable
          data={pageData?.content || []}
          columns={columns}
          rowId="uuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage="No security events found."
          onRefresh={() => refetch()}
          pagination={pageData ? {
            page: page,
            size: size,
            totalElements: pageData.totalElements,
            totalPages: Math.ceil(pageData.totalElements / size),
            onPageChange: setPage,
            onSizeChange: (s) => { setSize(s); setPage(0) }
          } : undefined}
        />
      </Section>
    </ContentContainer>
  )
}
