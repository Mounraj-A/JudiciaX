// ─── AdminNotificationsPage — Phase F10 ──────────────────────────────────────
import React, { useState } from 'react'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge } from '@/shared/components/badges'
import { AdminTable } from '../components/AdminTable'
import { useAdminNotifications, useMarkAllNotificationsRead, useDeleteNotification, useRetryFailedNotificationEvents } from '../hooks/useAdminNotifications'
import { toast } from 'react-hot-toast'
import { formatDistanceToNow } from 'date-fns'

export function AdminNotificationsPage() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)

  const { data: pageData, isLoading, error, refetch } = useAdminNotifications(page, size)
  const markAllReadMutation = useMarkAllNotificationsRead()
  const deleteMutation = useDeleteNotification()
  const retryMutation = useRetryFailedNotificationEvents()

  const handleMarkAllRead = async () => {
    try {
      await markAllReadMutation.mutateAsync()
      toast.success('All notifications marked as read')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const handleDelete = async (uuid: string) => {
    try {
      await deleteMutation.mutateAsync(uuid)
      toast.success('Notification deleted')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const handleRetry = async () => {
    try {
      await retryMutation.mutateAsync()
      toast.success('Triggered retry for failed events')
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const columns = [
    {
      id: 'status', header: '', width: 40,
      renderCell: (val: any, row: any) => (
        <div style={{ width: 8, height: 8, borderRadius: '50%', background: row.isRead ? 'transparent' : '#3B82F6', margin: '0 auto' }} />
      )
    },
    { id: 'title', header: 'Title', field: 'title', width: 250 },
    { id: 'message', header: 'Message', field: 'message' },
    {
      id: 'priority', header: 'Priority',
      renderCell: (val: any, row: any) => {
        const p = row.priority
        const map: any = { LOW: 'secondary', MEDIUM: 'info', HIGH: 'warning', CRITICAL: 'danger' }
        return <StatusBadge status={map[p] || 'secondary'} label={p} />
      }
    },
    { id: 'type', header: 'Type', field: 'type' },
    { id: 'time', header: 'Time', renderCell: (val: any, row: any) => formatDistanceToNow(new Date(row.createdAt), { addSuffix: true }) },
    {
      id: 'actions', header: 'Actions', align: 'right',
      renderCell: (val: any, row: any) => (
        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
          <Button variant="ghost" style={{ color: '#DC2626' }} onClick={() => handleDelete(row.uuid)}>Delete</Button>
        </div>
      )
    }
  ]

  return (
    <ContentContainer>
      <PageHeader
        title="Notification Center"
        subtitle="System-wide notifications and template management."
        actions={
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <Button variant="ghost" onClick={handleRetry} disabled={retryMutation.isPending}>
              Retry Failed Deliveries
            </Button>
            <Button variant="secondary" onClick={handleMarkAllRead} disabled={markAllReadMutation.isPending}>
              Mark All Read
            </Button>
          </div>
        }
      />

      <Section>
        <AdminTable
          data={pageData?.content || []}
          columns={columns}
          rowId="uuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage="No notifications found."
          onRefresh={() => refetch()}
          pagination={pageData ? {
            page: pageData.number,
            size: pageData.size,
            totalElements: pageData.totalElements,
            totalPages: pageData.totalPages,
            onPageChange: setPage,
            onSizeChange: (s) => { setSize(s); setPage(0) }
          } : undefined}
        />
      </Section>
    </ContentContainer>
  )
}
