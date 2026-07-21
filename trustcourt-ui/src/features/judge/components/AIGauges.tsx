// ─── AIGauges — Phase F9 ────────────────────────────────────────────────────────
import { CardSection } from '@/shared/components/layout'

// Simple SVG Circular Progress
function CircularProgress({ value, color }: { value: number; color: string }) {
  const radius = 36
  const circumference = 2 * Math.PI * radius
  const strokeDashoffset = circumference - (value / 100) * circumference
  const colorClass = {
    primary: 'text-brand-600 dark:text-brand-400',
    success: 'text-emerald-500',
    warning: 'text-amber-500',
    danger: 'text-rose-500'
  }[color] || 'text-brand-600'

  return (
    <svg className="w-24 h-24 transform -rotate-90">
      <circle
        cx="48"
        cy="48"
        r={radius}
        className="stroke-current text-slate-200 dark:text-slate-700"
        strokeWidth="8"
        fill="transparent"
      />
      <circle
        cx="48"
        cy="48"
        r={radius}
        className={`stroke-current ${colorClass} transition-all duration-1000 ease-out`}
        strokeWidth="8"
        fill="transparent"
        strokeDasharray={circumference}
        strokeDashoffset={strokeDashoffset}
        strokeLinecap="round"
      />
    </svg>
  )
}

interface GaugeProps {
  value: number
  title: string
  subtitle?: string
  color?: 'primary' | 'success' | 'warning' | 'danger'
}

export function AIGauge({ value, title, subtitle, color = 'primary' }: GaugeProps) {
  return (
    <CardSection className="flex flex-col items-center justify-center p-4 h-full">
      <h4 className="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-2">{title}</h4>
      <div className="relative flex items-center justify-center w-24 h-24 mb-2">
        <CircularProgress value={value} color={color} />
        <span className="absolute text-lg font-bold text-slate-900 dark:text-white">
          {value}%
        </span>
      </div>
      {subtitle && <span className="text-xs text-slate-500 dark:text-slate-400 text-center">{subtitle}</span>}
    </CardSection>
  )
}

export function JudicialPriorityGauge({ value }: { value: number }) {
  const color = value > 80 ? 'danger' : value > 50 ? 'warning' : 'primary'
  return <AIGauge value={value} title="Judicial Priority Index" subtitle="Based on urgency and impact" color={color} />
}

export function TrustScoreGauge({ value }: { value: number }) {
  const color = value > 80 ? 'success' : value > 50 ? 'warning' : 'danger'
  return <AIGauge value={value} title="Case Trust Score" subtitle="Completeness & consistency" color={color} />
}

export function ConfidenceMeter({ value }: { value: number }) {
  const color = value > 85 ? 'success' : value > 60 ? 'warning' : 'danger'
  return <AIGauge value={value} title="AI Confidence" subtitle="Recommendation certainty" color={color} />
}
