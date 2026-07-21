// ─── JudgeNotificationsPage — Phase F9 ──────────────────────────────────────
import { PageHeader, ContentContainer } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'

const MOCK_NOTIFICATIONS = [
  { id: 1, title: 'Urgent Filing', desc: 'Case 2026-1011 requires immediate attention. Limitation expires tomorrow.', time: '10 mins ago', type: 'urgent' },
  { id: 2, title: 'AI Analysis Complete', desc: 'Trust Score calculated for Case 2026-0902', time: '1 hour ago', type: 'info' },
  { id: 3, title: 'New Case Assigned', desc: 'Case 2026-1055 has been assigned to your bench.', time: '3 hours ago', type: 'standard' },
]

export function JudgeNotificationsPage() {
  return (
    <ContentContainer maxWidth="800px">
      <PageHeader title="Judicial Notifications" description="Updates and alerts for your assigned cases." />
      
      <div className="space-y-3">
        {MOCK_NOTIFICATIONS.map(n => (
          <div key={n.id} className="p-4 border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 rounded-lg flex justify-between items-start">
            <div className="flex flex-col">
              <span className="font-semibold text-slate-800 dark:text-slate-200">{n.title}</span>
              <span className="text-sm text-slate-600 dark:text-slate-400 mt-1">{n.desc}</span>
              <span className="text-xs text-slate-400 mt-2">{n.time}</span>
            </div>
            {n.type === 'urgent' && <StatusBadge status="error" label="Urgent" />}
            {n.type === 'info' && <StatusBadge status="info" label="Update" />}
          </div>
        ))}
      </div>
    </ContentContainer>
  )
}
