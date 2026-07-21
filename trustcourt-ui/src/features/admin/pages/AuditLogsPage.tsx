// ─── AuditLogsPage — Phase F10 ───────────────────────────────────────────────
import React, { useState } from 'react'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge } from '@/shared/components/badges'
import { AdminTable } from '../components/AdminTable'
import { useSearchAuditLogs, useExportAuditLogs } from '../hooks/useAdminAudit'
import { toast } from 'react-hot-toast'
import type { AuditSearchRequest } from '../api/adminAuditApi'

export function AuditLogsPage() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)

  const [searchParams, setSearchParams] = useState<AuditSearchRequest>({
    page: 0, size: 20
  })

  // Trigger search on submit
  const { data: pageData, isLoading, error, refetch } = useSearchAuditLogs({ ...searchParams, page, size })
  const exportMutation = useExportAuditLogs()

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setPage(0)
    refetch()
  }

  const handleExport = async () => {
    try {
      const blob = await exportMutation.mutateAsync({ format: 'CSV', startDate: searchParams.startDate, endDate: searchParams.endDate })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `audit_logs_${new Date().toISOString().split('T')[0]}.csv`
      a.click()
      toast.success('Audit logs exported successfully')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const columns = [
    { id: 'time', header: 'Timestamp', field: 'createdAt', width: 160 },
    { id: 'actor', header: 'Actor', field: 'actorUsername', width: 150 },
    { id: 'action', header: 'Action', field: 'action' },
    { id: 'entity', header: 'Entity', renderCell: (_: any, row: any) => `${row.entityType} (${row.entityUuid?.substring(0,8)})` },
    {
      id: 'severity', header: 'Severity', width: 100,
      renderCell: (_: any, row: any) => {
        const s = row.severity
        const map: any = { LOW: 'info', MEDIUM: 'warning', HIGH: 'danger', CRITICAL: 'danger' }
        return <StatusBadge status={map[s] || 'info'} label={s} />
      }
    },
    { id: 'ip', header: 'IP Address', field: 'ipAddress', width: 130 },
    {
      id: 'actions', header: 'Actions', align: 'right', width: 100,
      renderCell: (_: any, row: any) => (
        <Button variant="ghost" onClick={() => console.log('View details', row)}>View</Button>
      )
    }
  ]

  return (
    <ContentContainer>
      <PageHeader
        title="Enterprise Audit Logs"
        subtitle="Immutable record of system events, access, and modifications."
        actions={
          <Button variant="secondary" onClick={handleExport} disabled={exportMutation.isPending || !pageData?.content?.length}>
            ↓ Export Logs
          </Button>
        }
      />

      <Section>
        <form onSubmit={handleSearch} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr auto', gap: '1rem', marginBottom: '1.5rem', background: '#F9FAFB', padding: '1rem', borderRadius: '0.5rem', border: '1px solid #E5E7EB' }}>
          <div>
            <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#4B5563', textTransform: 'uppercase' }}>Action</label>
            <input type="text" value={searchParams.action || ''} onChange={(e) => setSearchParams(p => ({ ...p, action: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} placeholder="e.g. USER_LOGIN" />
          </div>
          <div>
            <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#4B5563', textTransform: 'uppercase' }}>Entity Type</label>
            <input type="text" value={searchParams.entityType || ''} onChange={(e) => setSearchParams(p => ({ ...p, entityType: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} placeholder="e.g. CASE" />
          </div>
          <div>
            <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#4B5563', textTransform: 'uppercase' }}>Severity</label>
            <select value={searchParams.severity || ''} onChange={(e) => setSearchParams(p => ({ ...p, severity: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}>
              <option value="">All</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>
          <div>
            <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#4B5563', textTransform: 'uppercase' }}>Date Range</label>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input type="date" value={searchParams.startDate || ''} onChange={(e) => setSearchParams(p => ({ ...p, startDate: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
            </div>
          </div>
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <Button type="submit">Search</Button>
          </div>
        </form>

        <AdminTable
          data={pageData?.content || []}
          columns={columns}
          rowId="uuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage="No audit logs found matching criteria."
          pagination={pageData ? {
            page,
            size,
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
