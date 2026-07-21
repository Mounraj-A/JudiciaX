// ─── AdminReportsPage — Phase F10 ──────────────────────────────────────────
import React, { useState } from 'react'
import { PageHeader, ContentContainer, Section, GridLayout, CardSection } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { LoadingLayout, ErrorLayout } from '@/shared/components/layout/StateLayouts'
import { AdminTable } from '../components/AdminTable'
import { useCaseStatusDistribution, useJudgePerformanceScores, useExportReport } from '../hooks/useAdminReports'
import { toast } from 'react-hot-toast'

export function AdminReportsPage() {
  const [activeTab, setActiveTab] = useState<'cases' | 'judges'>('cases')

  const { data: caseStats, isLoading: loadingCases, error: errorCases } = useCaseStatusDistribution()
  const { data: judgeScores, isLoading: loadingJudges, error: errorJudges } = useJudgePerformanceScores()
  
  const exportMutation = useExportReport()

  const handleExport = async (reportType: string) => {
    try {
      const blob = await exportMutation.mutateAsync({ reportType, format: 'CSV' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${reportType}_report_${new Date().toISOString().split('T')[0]}.csv`
      a.click()
      toast.success('Report exported successfully')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const renderCases = () => {
    if (loadingCases) return <LoadingLayout />
    if (errorCases) return <ErrorLayout message={errorCases.message} />

    const columns = [
      { id: 'status', header: 'Case Status', field: 'status' },
      { id: 'count', header: 'Count', field: 'count' },
      { id: 'percentage', header: 'Percentage', renderCell: (val: any, row: any) => `${row.percentage}%` },
    ]

    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        <GridLayout cols={3}>
          {caseStats?.map((s) => (
            <CardSection key={s.status} title={s.status} className="text-center">
              <div style={{ fontSize: '2rem', fontWeight: 700, color: '#0F1D3A' }}>{s.count}</div>
              <div style={{ fontSize: '0.875rem', color: '#6B7280' }}>{s.percentage}% of total</div>
            </CardSection>
          ))}
        </GridLayout>
        <AdminTable data={caseStats || []} columns={columns} rowId="status" />
      </div>
    )
  }

  const renderJudges = () => {
    if (loadingJudges) return <LoadingLayout />
    if (errorJudges) return <ErrorLayout message={errorJudges.message} />

    const columns = [
      { id: 'name', header: 'Judge Name', field: 'fullName' },
      { id: 'court', header: 'Court', field: 'court' },
      { id: 'assigned', header: 'Total Assigned', field: 'totalAssigned' },
      { id: 'disposed', header: 'Total Disposed', field: 'totalDisposed' },
      { id: 'rate', header: 'Disposal Rate', renderCell: (val: any, row: any) => `${row.disposalRate}%` },
      { id: 'avgDays', header: 'Avg Disposal Days', field: 'avgDisposalDays' },
      { id: 'score', header: 'Performance Score', renderCell: (val: any, row: any) => <strong style={{ color: row.performanceScore > 80 ? '#10B981' : '#3B82F6' }}>{row.performanceScore}/100</strong> },
    ]

    return <AdminTable data={judgeScores || []} columns={columns} rowId="judgeUuid" />
  }

  return (
    <ContentContainer>
      <PageHeader
        title="Enterprise Analytics & Reports"
        subtitle="Platform-wide operational analytics, performance metrics, and reporting."
        actions={
          <Button variant="secondary" onClick={() => handleExport(activeTab)} disabled={exportMutation.isPending}>
            ↓ Export Current View
          </Button>
        }
      />

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid #E5E7EB', marginBottom: '1.5rem', paddingBottom: '0.5rem' }}>
        {[
          { id: 'cases', label: 'Case Distribution' },
          { id: 'judges', label: 'Judge Performance' }
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id as any)}
            style={{
              padding: '0.5rem 1rem', fontSize: '0.875rem', fontWeight: 600,
              color: activeTab === tab.id ? '#0F1D3A' : '#6B7280',
              borderBottom: activeTab === tab.id ? '2px solid #0F1D3A' : 'none',
              background: 'none', cursor: 'pointer'
            }}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <Section>
        {activeTab === 'cases' && renderCases()}
        {activeTab === 'judges' && renderJudges()}
      </Section>
    </ContentContainer>
  )
}
