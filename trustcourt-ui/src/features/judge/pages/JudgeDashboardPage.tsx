// ─── JudgeDashboardPage — Phase F9 ──────────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { ContentContainer, Section } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'

const MOCK_STATS = [
  { label: "Today's Hearings", value: '8', change: '+2' },
  { label: 'Pending Cases', value: '124', change: '-5' },
  { label: 'Reserved Judgments', value: '12', change: '0' },
  { label: 'Disposed Cases (Month)', value: '45', change: '+12%' },
]

export function JudgeDashboardPage() {
  const navigate = useNavigate()
  return (
    <ContentContainer>
      <PageHeader 
        title="Judge Dashboard" 
        description="Welcome to your judicial workspace. Overview of your workload and upcoming schedule." 
      />
      
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        {MOCK_STATS.map((stat, i) => (
          <div key={i} className="p-4 rounded-xl border bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-700 shadow-sm">
            <p className="text-sm text-slate-500 dark:text-slate-400 font-medium">{stat.label}</p>
            <div className="flex items-baseline gap-2 mt-1">
              <span className="text-2xl font-bold text-slate-900 dark:text-white">{stat.value}</span>
              <span className={`text-xs font-semibold ${stat.change.startsWith('+') ? 'text-emerald-600' : stat.change === '0' ? 'text-slate-400' : 'text-rose-600'}`}>
                {stat.change}
              </span>
            </div>
          </div>
        ))}
      </div>

      <Section title="High Priority Cases" bordered padded>
        <div className="space-y-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="flex justify-between items-center p-3 border rounded-lg bg-slate-50 dark:bg-slate-800/50 cursor-pointer hover:bg-slate-100 transition-colors" onClick={() => navigate(ROUTES.JUDGE.REVIEW.replace(':id', `Case-${i}`))}>
              <div className="flex flex-col">
                <span className="font-semibold text-brand-700 dark:text-brand-400">STATE vs. DEFENDANT {i}</span>
                <span className="text-xs text-slate-500">Limitation expires in {i} days</span>
              </div>
              <div className="flex items-center gap-3">
                <StatusBadge status="warning" label={`JPI: ${90 - i * 5}%`} />
                <StatusBadge status="info" label="Ready for Hearing" />
              </div>
            </div>
          ))}
        </div>
      </Section>
    </ContentContainer>
  )
}
