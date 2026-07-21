// â”€â”€â”€ WorkflowMonitoringPage â€” Phase F10 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { PageHeader, ContentContainer, Section, GridLayout, CardSection } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge } from '@/shared/components/badges'
import { AdminTable } from '../components/AdminTable'
import { useAdminWorkflows, useWorkflowStatus, useRetryWorkflow, useCancelWorkflow, useRestartWorkflow } from '../hooks/useAdminWorkflow'
import { toast } from 'react-hot-toast'

export function WorkflowMonitoringPage() {
  const [page, _setPage] = useState(0)
  const [size, _setSize] = useState(20)

  const { data: pageData, isLoading, error, refetch } = useAdminWorkflows(page, size)
  const { data: statusStats } = useWorkflowStatus()

  const retryMutation = useRetryWorkflow()
  const cancelMutation = useCancelWorkflow()
  const restartMutation = useRestartWorkflow()

  const handleAction = async (action: 'retry' | 'cancel' | 'restart', uuid: string) => {
    try {
      if (action === 'retry') {
        await retryMutation.mutateAsync(uuid)
        toast.success('Workflow queued for retry')
      } else if (action === 'cancel') {
        await cancelMutation.mutateAsync(uuid)
        toast.success('Workflow cancelled')
      } else if (action === 'restart') {
        await restartMutation.mutateAsync(uuid)
        toast.success('Workflow restarted')
      }
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const columns = [
    { id: 'uuid', header: 'Workflow ID', renderCell: (_: any, row: any) => <span style={{ fontFamily: 'monospace', fontSize: '0.75rem' }}>{row.uuid.substring(0, 8)}...</span> },
    { id: 'type', header: 'Type', field: 'workflowType' },
    {
      id: 'status', header: 'Status',
      renderCell: (_: any, row: any) => {
        const s = row.status
        const map: any = { RUNNING: 'primary', QUEUED: 'info', COMPLETED: 'success', FAILED: 'danger', CANCELLED: 'secondary' }
        return <StatusBadge status={map[s] || 'info'} label={s} />
      }
    },
    { id: 'startedAt', header: 'Started At', field: 'startedAt' },
    { id: 'retryCount', header: 'Retries', field: 'retryCount' },
    {
      id: 'actions', header: 'Actions', align: 'right',
      renderCell: (_: any, row: any) => (
        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
          {row.status === 'FAILED' && <Button variant="ghost" onClick={() => handleAction('retry', row.uuid)}>Retry</Button>}
          {(row.status === 'RUNNING' || row.status === 'QUEUED') && (
            <Button variant="ghost" style={{ color: '#DC2626' }} onClick={() => handleAction('cancel', row.uuid)}>Cancel</Button>
          )}
          {(row.status === 'COMPLETED' || row.status === 'CANCELLED') && (
            <Button variant="ghost" onClick={() => handleAction('restart', row.uuid)}>Restart</Button>
          )}
        </div>
      )
    }
  ]

  return (
    <ContentContainer>
      <PageHeader
        title="Workflow Monitoring"
        subtitle="Live monitor for AI pipelines, background tasks, and asynchronous operations."
        actions={<Button variant="ghost" onClick={() => refetch()}>â†» Refresh</Button>}
      />

      <GridLayout cols={4}>
        <CardSection title="Running" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#3B82F6' }}>{statusStats?.running || 0}</div>
        </CardSection>
        <CardSection title="Queued" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#8B5CF6' }}>{statusStats?.queued || 0}</div>
        </CardSection>
        <CardSection title="Completed" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#10B981' }}>{statusStats?.completed || 0}</div>
        </CardSection>
        <CardSection title="Failed" className="text-center">
          <div style={{ fontSize: '2rem', fontWeight: 700, color: '#EF4444' }}>{statusStats?.failed || 0}</div>
        </CardSection>
      </GridLayout>

      <Section className="mt-6">
        <AdminTable
          data={pageData || []}
          columns={columns}
          rowId="uuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage="No workflows found."
          onRefresh={() => refetch()}
        />
      </Section>
    </ContentContainer>
  )
}
