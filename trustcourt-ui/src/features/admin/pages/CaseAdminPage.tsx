// ─── CaseAdminPage — Phase F10 ───────────────────────────────────────────────
import { useState } from 'react'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { AdminTable } from '../components/AdminTable'
import { useCaseStatusDistribution } from '../hooks/useAdminReports'

export function CaseAdminPage() {
  const { data: statusDist, isLoading, error, refetch } = useCaseStatusDistribution()

  const columns = [
    { id: 'status', header: 'Case Status', field: 'status' },
    { id: 'count', header: 'Count', field: 'count' },
    {
      id: 'percentage', header: 'Percentage',
      renderCell: (_: any, row: any) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <div style={{ width: '100px', height: '6px', background: '#E5E7EB', borderRadius: '3px' }}>
            <div style={{ width: `${row.percentage}%`, height: '100%', background: '#3B82F6', borderRadius: '3px' }} />
          </div>
          <span style={{ fontSize: '0.8125rem' }}>{row.percentage}%</span>
        </div>
      )
    },
    {
      id: 'actions', header: 'Actions', align: 'right',
      renderCell: (_: any, row: any) => (
        <Button variant="ghost">View Cases</Button>
      )
    }
  ]

  return (
    <ContentContainer>
      <PageHeader
        title="Global Case Registry"
        subtitle="View case distribution across courts and system-wide backlogs."
      />

      <Section title="Case Status Distribution">
        <AdminTable
          data={statusDist || []}
          columns={columns}
          rowId="status"
          loading={isLoading}
          error={error?.message}
          emptyMessage="No case data available."
          onRefresh={() => refetch()}
        />
      </Section>
    </ContentContainer>
  )
}
