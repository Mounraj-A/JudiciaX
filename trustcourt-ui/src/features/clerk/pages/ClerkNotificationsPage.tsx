// ─── ClerkNotificationsPage — Phase F8 ────────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'

export function ClerkNotificationsPage() {
  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Notifications" 
        description="System alerts, case updates, and workflow triggers for your scrutiny queue." 
        actions={<button style={{ padding: '0.5rem 1rem', background: '#FFFFFF', border: '1px solid #D1D5DB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 600, cursor: 'pointer' }}>Mark All Read</button>}
      />

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid #E5E7EB', paddingBottom: '1rem' }}>
        <button style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', border: 'none', borderRadius: '20px', fontSize: '0.875rem', fontWeight: 600, cursor: 'pointer' }}>Unread (3)</button>
        <button style={{ padding: '0.5rem 1rem', background: 'transparent', color: '#6B7280', border: 'none', fontSize: '0.875rem', fontWeight: 500, cursor: 'pointer' }}>All</button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {[
          { id: 1, title: 'New High Priority Submission', time: '10 mins ago', type: 'System', desc: 'Case Sub-2026-082 has been flagged for urgent scrutiny.', unread: true },
          { id: 2, title: 'Advocate Resubmitted Case', time: '1 hr ago', type: 'Workflow', desc: 'Sub-2026-052 (Returned) has been updated and resubmitted by Adv. M. Sharma.', unread: true },
          { id: 3, title: 'AI OCR Processing Complete', time: '3 hrs ago', type: 'AI', desc: 'Documents for Sub-2026-083 are fully processed and ready for verification.', unread: true },
          { id: 4, title: 'Judge Assigned to CR/2026/0091', time: '1 day ago', type: 'System', desc: 'Hon. Justice A. Patel assigned to newly registered case.', unread: false },
        ].map(n => (
          <div key={n.id} style={{ padding: '1.5rem', background: n.unread ? '#F0FDF4' : '#FFF', border: `1px solid ${n.unread ? '#86EFAC' : '#E5E7EB'}`, borderRadius: '12px', display: 'flex', gap: '1.5rem' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '50%', background: n.unread ? '#10B981' : '#F3F4F6', color: n.unread ? '#FFF' : '#6B7280', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.25rem' }}>
              {n.type === 'System' ? '⚙️' : n.type === 'AI' ? '🧠' : '📋'}
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <h4 style={{ margin: '0 0 0.5rem 0', fontSize: '1.125rem', color: '#111827' }}>{n.title}</h4>
                <span style={{ fontSize: '0.875rem', color: '#6B7280' }}>{n.time}</span>
              </div>
              <p style={{ margin: '0 0 1rem 0', color: '#4B5563', fontSize: '0.9375rem' }}>{n.desc}</p>
              <StatusBadge status={n.unread ? 'success' : 'default'} />
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
