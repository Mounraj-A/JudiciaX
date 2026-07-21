// ─── FeatureImportanceChart — Phase F9 ─────────────────────────────────────────
import { CardSection } from '@/shared/components/layout'

interface FeatureImportanceProps {
  features: { name: string; score: number; color?: string }[]
}

export function FeatureImportanceChart({ features }: FeatureImportanceProps) {
  return (
    <CardSection className="p-4 w-full h-full">
      <h4 className="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-4">
        Top Contributing Factors
      </h4>
      <div className="space-y-3">
        {features.map((f, i) => (
          <div key={i} className="flex flex-col gap-1">
            <div className="flex justify-between text-xs">
              <span className="font-medium text-slate-700 dark:text-slate-200">{f.name}</span>
              <span className="text-slate-500 dark:text-slate-400">{(f.score * 100).toFixed(0)}%</span>
            </div>
            <div className="w-full h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
              <div 
                className={`h-full ${f.color || 'bg-brand-500'} transition-all duration-1000 ease-out`}
                style={{ width: `${f.score * 100}%` }}
              />
            </div>
          </div>
        ))}
      </div>
    </CardSection>
  )
}
