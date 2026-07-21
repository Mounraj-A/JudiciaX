// â”€â”€â”€ AdvocateNotificationsPage â€” Phase F7 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { PageHeader } from '@/shared/components/layout'
import { useAdvocateNotifications, useMarkAllNotificationsRead, useMarkNotificationRead } from '@/features/advocate/api/useAdvocateNotifications'

export function AdvocateNotificationsPage() {
  const [filter, setFilter] = useState<'all' | 'unread'>('all')
  const [page] = useState(0)
  const [size] = useState(20)

  const unreadOnly = filter === 'unread'
  const { data: notificationsData, isLoading, error } = useAdvocateNotifications(page, size, unreadOnly)
  const { mutate: markAllRead } = useMarkAllNotificationsRead()
  const { mutate: markRead } = useMarkNotificationRead()

  const notifications = notificationsData?.data || []

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Notifications" 
        description="Important alerts from the Registry, AI Platform, and Court." 
        actions={<button onClick={() => markAllRead()} style={{ padding: '0.5rem 1rem', background: '#FFFFFF', border: '1px solid #D1D5DB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 600, cursor: 'pointer' }}>Mark All Read</button>}
      />

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid #E5E7EB', paddingBottom: '1rem' }}>
        <button 
          onClick={() => setFilter('all')} 
          style={{ padding: '0.5rem 1rem', background: filter === 'all' ? '#0F1D3A' : '#F9FAFB', color: filter === 'all' ? '#FFF' : '#374151', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 600, cursor: 'pointer' }}
        >
          All
        </button>
        <button 
          onClick={() => setFilter('unread')} 
          style={{ padding: '0.5rem 1rem', background: filter === 'unread' ? '#0F1D3A' : '#F9FAFB', color: filter === 'unread' ? '#FFF' : '#374151', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 600, cursor: 'pointer' }}
        >
          Unread
        </button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {isLoading ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#6B7280' }}>Loading notifications...</div>
        ) : error ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: 'red' }}>Failed to load notifications.</div>
        ) : notifications.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: '#6B7280' }}>You're all caught up!</div>
        ) : (
          notifications.map((n: any) => (
            <div 
              key={n.uuid} 
              onClick={() => !n.isRead && markRead(n.uuid)}
              style={{ display: 'flex', gap: '1rem', background: n.isRead ? '#FFFFFF' : '#EFF6FF', border: `1px solid ${n.isRead ? '#E5E7EB' : '#BFDBFE'}`, padding: '1.5rem', borderRadius: '12px', cursor: n.isRead ? 'default' : 'pointer' }}
            >
              {!n.isRead && <div style={{ width: 12, height: 12, borderRadius: '50%', background: '#3B82F6', marginTop: 6 }} />}
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                  <h4 style={{ margin: 0, fontSize: '1rem', color: '#111827' }}>{n.title}</h4>
                  <span style={{ fontSize: '0.75rem', color: '#6B7280' }}>{new Date(n.createdAt).toLocaleString()}</span>
                </div>
                <p style={{ margin: 0, fontSize: '0.875rem', color: '#4B5563' }}>{n.message}</p>
                {n.referenceId && (
                  <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.75rem', color: '#9CA3AF' }}>Ref: {n.referenceId}</p>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
export default AdvocateNotificationsPage
