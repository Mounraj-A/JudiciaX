// ─── TrustRadar — Phase F9 ───────────────────────────────────────────────────
import { CardSection } from '@/shared/components/layout'

interface RadarData {
  labels: string[]
  scores: number[] // 0-100
}

export function TrustRadar({ labels, scores }: RadarData) {
  // A simple CSS-based mock radar or a stylized list for now, 
  // since we don't have Recharts installed in UI layer by default.
  // In a real app we'd use Chart.js or Recharts for a radar.
  return (
    <CardSection className="p-4 w-full h-full flex flex-col">
      <h4 className="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-4">
        Trust Factor Radar
      </h4>
      <div className="flex-1 flex flex-col justify-center space-y-2">
        {labels.map((label, idx) => (
          <div key={idx} className="flex items-center justify-between text-sm">
            <span className="text-slate-600 dark:text-slate-400">{label}</span>
            <div className="flex items-center gap-2">
              <div className="w-24 h-1.5 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                <div 
                  className="h-full bg-emerald-500 rounded-full"
                  style={{ width: `${scores[idx]}%` }}
                />
              </div>
              <span className="text-xs font-semibold text-slate-700 dark:text-slate-300 w-6 text-right">
                {scores[idx]}
              </span>
            </div>
          </div>
        ))}
      </div>
    </CardSection>
  )
}
