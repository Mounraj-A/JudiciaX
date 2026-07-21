// ─── DecisionTimeline — Phase F9 ─────────────────────────────────────────────
import { CardSection } from '@/shared/components/layout'

interface TimelineEvent {
  id: string
  date: string
  title: string
  description: string
  status: 'completed' | 'current' | 'pending'
}

export function DecisionTimeline({ events }: { events: TimelineEvent[] }) {
  return (
    <CardSection className="p-4 w-full h-full">
      <h4 className="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-4">
        Workflow Progress
      </h4>
      <div className="relative border-l border-slate-200 dark:border-slate-700 ml-3 space-y-6">
        {events.map((evt) => {
          const isCompleted = evt.status === 'completed'
          const isCurrent = evt.status === 'current'
          return (
            <div key={evt.id} className="relative pl-6">
              <div 
                className={`absolute left-[-5px] top-1 w-2.5 h-2.5 rounded-full ring-4 ring-white dark:ring-slate-900 ${
                  isCompleted ? 'bg-brand-500' : isCurrent ? 'bg-amber-500 ring-amber-100 dark:ring-amber-900/30' : 'bg-slate-300 dark:bg-slate-600'
                }`} 
              />
              <div className="flex flex-col">
                <span className="text-xs font-semibold text-slate-500 dark:text-slate-400 mb-0.5">{evt.date}</span>
                <span className={`text-sm font-medium ${isCurrent ? 'text-brand-600 dark:text-brand-400' : 'text-slate-800 dark:text-slate-200'}`}>
                  {evt.title}
                </span>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
                  {evt.description}
                </p>
              </div>
            </div>
          )
        })}
      </div>
    </CardSection>
  )
}
