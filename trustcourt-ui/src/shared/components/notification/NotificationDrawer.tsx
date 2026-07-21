// â”€â”€â”€ NotificationDrawer â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useNotificationContext } from '@/shared/contexts/notification/NotificationContext'
import { NotificationItem } from '../timeline'
import { EmptyLayout } from '../layout/StateLayouts'

export function NotificationDrawer() {
  const { state, toggleDrawer, markRead, markAllRead } = useNotificationContext()

  if (!state.isOpen) return null

  return (
    <div style={{ position: 'fixed', inset: 0, zIndex: 9999, display: 'flex', justifyContent: 'flex-end' }}>
      <div
        style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.4)' }}
        onClick={toggleDrawer}
      />
      <div style={{
        position: 'relative', width: '380px', maxWidth: '100vw', background: '#FFFFFF',
        height: '100%', display: 'flex', flexDirection: 'column',
        boxShadow: '-4px 0 24px rgba(0,0,0,0.1)',
        animation: 'slideInRight 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
      }}>
        <div style={{
          padding: '1.25rem 1.5rem', borderBottom: '1px solid #E5E7EB',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between'
        }}>
          <div>
            <h2 style={{ fontSize: '1.125rem', fontWeight: 600, color: '#111827', margin: 0 }}>Notifications</h2>
            {state.unreadCount > 0 && (
              <p style={{ fontSize: '0.8125rem', color: '#6B7280', margin: '0.25rem 0 0' }}>
                You have {state.unreadCount} unread message(s)
              </p>
            )}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            {state.unreadCount > 0 && (
              <button onClick={markAllRead} style={{ fontSize: '0.75rem', color: '#0F1D3A', background: 'none', border: 'none', cursor: 'pointer' }}>
                Mark all read
              </button>
            )}
            <button onClick={toggleDrawer} style={{ background: 'none', border: 'none', fontSize: '1.25rem', color: '#9CA3AF', cursor: 'pointer' }}>Ã—</button>
          </div>
        </div>

        <div style={{ flex: 1, overflowY: 'auto', padding: '1rem 1.5rem' }}>
          {state.items.length === 0 ? (
            <EmptyLayout title="No notifications" description="You're all caught up." compact />
          ) : (
            state.items.map((item, idx) => (
              <NotificationItem
                key={item.id}
                title={item.title}
                message={item.message}
                category={item.category}
                timestamp={item.createdAt}
                read={item.read}
                onRead={() => markRead(item.id)}
                last={idx === state.items.length - 1}
              />
            ))
          )}
        </div>
      </div>
      <style>{`@keyframes slideInRight { from { transform: translateX(100%) } to { transform: translateX(0) } }`}</style>
    </div>
  )
}
