// ─── RiskMatrix — Phase F9 ───────────────────────────────────────────────────
import { CardSection } from '@/shared/components/layout'

interface RiskIndicator {
  type: string
  level: 'low' | 'medium' | 'high'
  description: string
}

const colorMap = {
  low: 'text-emerald-700 bg-emerald-50 border-emerald-200 dark:bg-emerald-900/20 dark:text-emerald-400 dark:border-emerald-800',
  medium: 'text-amber-700 bg-amber-50 border-amber-200 dark:bg-amber-900/20 dark:text-amber-400 dark:border-amber-800',
  high: 'text-rose-700 bg-rose-50 border-rose-200 dark:bg-rose-900/20 dark:text-rose-400 dark:border-rose-800',
}

export function RiskMatrix({ risks }: { risks: RiskIndicator[] }) {
  return (
    <CardSection className="p-4 w-full h-full">
      <h4 className="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-4">
        Procedural Risks
      </h4>
      <div className="space-y-2">
        {risks.map((risk, idx) => (
          <div 
            key={idx} 
            className={`p-3 rounded-md border text-sm flex flex-col gap-1 ${colorMap[risk.level]}`}
          >
            <div className="flex justify-between items-center font-medium">
              <span>{risk.type}</span>
              <span className="uppercase text-xs tracking-wider">{risk.level}</span>
            </div>
            <p className="text-xs opacity-90">{risk.description}</p>
          </div>
        ))}
      </div>
    </CardSection>
  )
}
