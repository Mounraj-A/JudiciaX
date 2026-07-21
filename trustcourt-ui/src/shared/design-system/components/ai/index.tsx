import { motion } from 'framer-motion'
import { cn } from '@/shared/design-system/utils/cn'
import { Badge } from '../primitives/Badge'

// ─── Priority Score Card ──────────────────────────────────────────────────────
interface PriorityCardProps {
  jpiScore: number
  caseId: string
  caseType?: string
  filingDate?: string
  onClick?: () => void
  compact?: boolean
}

function getPriorityMeta(score: number) {
  if (score >= 90) return { label: 'Emergency', color: '#B91C1C', bg: '#FEE2E2', border: '#FECACA', variant: 'emergency' as const }
  if (score >= 75) return { label: 'Critical',  color: '#EA580C', bg: '#FFEDD5', border: '#FED7AA', variant: 'critical'  as const }
  if (score >= 60) return { label: 'High',      color: '#D97706', bg: '#FEF3C7', border: '#FDE68A', variant: 'high'      as const }
  if (score >= 40) return { label: 'Medium',    color: '#0369A1', bg: '#E0F2FE', border: '#BAE6FD', variant: 'medium'    as const }
  return              { label: 'Low',           color: '#059669', bg: '#D1FAE5', border: '#A7F3D0', variant: 'low'       as const }
}

function PriorityScoreCard({ jpiScore, caseId, caseType, filingDate, onClick, compact }: PriorityCardProps) {
  const meta = getPriorityMeta(jpiScore)
  const pct = Math.min(100, Math.max(0, jpiScore))

  return (
    <motion.div
      whileHover={{ y: -2 }}
      onClick={onClick}
      className={cn(
        'bg-white border rounded-xl p-5 cursor-pointer',
        'border-l-4 shadow-soft hover:shadow-medium transition-all',
        onClick ? 'cursor-pointer' : 'cursor-default'
      )}
      style={{ borderLeftColor: meta.color }}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
      aria-label={`Case ${caseId}, JPI Score ${jpiScore}, Priority ${meta.label}`}
    >
      <div className="flex items-start justify-between mb-3">
        <div>
          <p className="text-xs font-medium text-[#6B7280] uppercase tracking-wide">JPI Score</p>
          <p className="text-2xl font-bold mt-0.5" style={{ color: meta.color }}>
            {jpiScore.toFixed(1)}
          </p>
        </div>
        <Badge variant={meta.variant}>{meta.label}</Badge>
      </div>

      {/* Score bar */}
      <div className="h-1.5 rounded-full bg-[#E5E7EB] overflow-hidden mb-3">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: `${pct}%` }}
          transition={{ duration: 0.8, ease: [0.4, 0, 0.2, 1], delay: 0.1 }}
          className="h-full rounded-full"
          style={{ backgroundColor: meta.color }}
        />
      </div>

      {!compact && (
        <div className="flex items-center justify-between text-xs text-[#6B7280]">
          <span className="font-mono">{caseId}</span>
          {caseType && <span>{caseType}</span>}
          {filingDate && <span>{filingDate}</span>}
        </div>
      )}
    </motion.div>
  )
}

// ─── Trust Score Card ─────────────────────────────────────────────────────────
interface TrustCardProps {
  ctsScore: number
  caseId: string
  factors?: { label: string; score: number }[]
  onClick?: () => void
}

function getTrustMeta(score: number) {
  if (score >= 80) return { label: 'High Trust',     color: '#059669', bg: '#D1FAE5' }
  if (score >= 50) return { label: 'Moderate Trust', color: '#D97706', bg: '#FEF3C7' }
  return              { label: 'Low Trust',          color: '#B91C1C', bg: '#FEE2E2' }
}

function TrustScoreCard({ ctsScore, caseId, factors, onClick }: TrustCardProps) {
  const meta = getTrustMeta(ctsScore)
  const pct = Math.min(100, Math.max(0, ctsScore))

  return (
    <motion.div
      whileHover={{ y: -2 }}
      onClick={onClick}
      className="bg-white border border-[#E5E7EB] rounded-xl p-5 shadow-soft hover:shadow-medium transition-all"
      style={{ borderTopColor: meta.color, borderTopWidth: '3px' }}
      aria-label={`CTS Score ${ctsScore} for case ${caseId}`}
    >
      <div className="flex items-start justify-between mb-3">
        <div>
          <p className="text-xs font-medium text-[#6B7280] uppercase tracking-wide">Trust Score</p>
          <p className="text-2xl font-bold mt-0.5" style={{ color: meta.color }}>
            {ctsScore.toFixed(1)}
          </p>
        </div>
        <div
          className="text-xs font-medium px-2.5 py-1 rounded-full"
          style={{ backgroundColor: meta.bg, color: meta.color }}
        >
          {meta.label}
        </div>
      </div>

      {/* Gauge */}
      <div className="relative h-2 rounded-full overflow-hidden mb-4" style={{ backgroundColor: '#E5E7EB' }}>
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: `${pct}%` }}
          transition={{ duration: 0.8, ease: [0.4, 0, 0.2, 1], delay: 0.1 }}
          className="absolute h-full rounded-full"
          style={{ backgroundColor: meta.color }}
        />
      </div>

      {/* Factor breakdown */}
      {factors && factors.length > 0 && (
        <ul className="space-y-1.5">
          {factors.map((f) => (
            <li key={f.label} className="flex items-center justify-between text-xs">
              <span className="text-[#6B7280]">{f.label}</span>
              <span className="font-medium text-[#374151]">{f.score.toFixed(0)}</span>
            </li>
          ))}
        </ul>
      )}
    </motion.div>
  )
}

// ─── Confidence Meter ─────────────────────────────────────────────────────────
interface ConfidenceMeterProps {
  value: number  // 0–100
  label?: string
  module?: 'xai' | 'jpi' | 'cts' | 'general'
  showLabel?: boolean
}

const confidenceColors = {
  xai:     '#6D28D9',
  jpi:     '#EA580C',
  cts:     '#0F766E',
  general: '#1E3A8A',
}

function ConfidenceMeter({ value, label = 'Confidence', module = 'general', showLabel = true }: ConfidenceMeterProps) {
  const color = confidenceColors[module]
  const pct = Math.min(100, Math.max(0, value))
  const qualitativeLabel = pct >= 85 ? 'High' : pct >= 60 ? 'Moderate' : 'Low'

  return (
    <div className="space-y-2" aria-label={`${label}: ${pct}% (${qualitativeLabel})`}>
      {showLabel && (
        <div className="flex justify-between text-xs font-medium">
          <span className="text-[#6B7280]">{label}</span>
          <span style={{ color }}>{qualitativeLabel} ({pct}%)</span>
        </div>
      )}
      <div className="relative h-2 rounded-full bg-[#E5E7EB] overflow-hidden">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: `${pct}%` }}
          transition={{ duration: 0.8, ease: [0.4, 0, 0.2, 1] }}
          className="absolute h-full rounded-full"
          style={{ backgroundColor: color }}
        />
      </div>
    </div>
  )
}

// ─── Risk Indicator ───────────────────────────────────────────────────────────
interface RiskIndicatorProps {
  severity: 'low' | 'medium' | 'high' | 'critical'
  title: string
  description?: string
}

const riskMeta = {
  low:      { color: '#059669', bg: '#D1FAE5', dot: 'bg-[#059669]' },
  medium:   { color: '#D97706', bg: '#FEF3C7', dot: 'bg-[#D97706]' },
  high:     { color: '#C2410C', bg: '#FFEDD5', dot: 'bg-[#C2410C]' },
  critical: { color: '#B91C1C', bg: '#FEE2E2', dot: 'bg-[#B91C1C]' },
}

function RiskIndicator({ severity, title, description }: RiskIndicatorProps) {
  const meta = riskMeta[severity]
  return (
    <div
      className="flex items-start gap-3 rounded-lg p-3 border"
      style={{ backgroundColor: meta.bg, borderColor: meta.color + '33' }}
      role="status"
      aria-label={`${severity} risk: ${title}`}
    >
      <span
        className={cn('mt-1 h-2 w-2 rounded-full shrink-0', meta.dot)}
        aria-hidden="true"
      />
      <div>
        <p className="text-sm font-medium" style={{ color: meta.color }}>{title}</p>
        {description && <p className="text-xs text-[#6B7280] mt-0.5">{description}</p>}
      </div>
    </div>
  )
}

// ─── Pipeline Status ──────────────────────────────────────────────────────────
type PipelineStageStatus = 'idle' | 'processing' | 'completed' | 'failed' | 'skipped'

interface PipelineStage {
  id: string
  label: string
  status: PipelineStageStatus
  duration?: string
  module: 'ocr' | 'nlp' | 'feature' | 'jpi' | 'cts' | 'xai' | 'decision' | 'governance'
}

const moduleColors: Record<PipelineStage['module'], string> = {
  ocr:        '#0891B2',
  nlp:        '#7C3AED',
  feature:    '#4338CA',
  jpi:        '#EA580C',
  cts:        '#0F766E',
  xai:        '#6D28D9',
  decision:   '#1D4ED8',
  governance: '#475569',
}

const statusIcons: Record<PipelineStageStatus, string> = {
  idle:       '○',
  processing: '◌',
  completed:  '✓',
  failed:     '✕',
  skipped:    '—',
}

function PipelineStatus({ stages }: { stages: PipelineStage[] }) {
  return (
    <div className="space-y-2" role="list" aria-label="AI Pipeline Status">
      {stages.map((stage, idx) => (
        <div key={stage.id} className="flex items-center gap-3" role="listitem">
          <div className="flex flex-col items-center">
            <div
              className={cn(
                'h-7 w-7 rounded-full flex items-center justify-center text-xs font-bold text-white',
                stage.status === 'processing' && 'animate-pulse-soft'
              )}
              style={{ backgroundColor: moduleColors[stage.module] }}
              aria-label={`${stage.label}: ${stage.status}`}
            >
              {statusIcons[stage.status]}
            </div>
            {idx < stages.length - 1 && (
              <div className="w-0.5 h-4 bg-[#E5E7EB] mt-1" aria-hidden="true" />
            )}
          </div>
          <div className="flex-1">
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-[#374151]">{stage.label}</span>
              {stage.duration && (
                <span className="text-xs text-[#9CA3AF]">{stage.duration}</span>
              )}
            </div>
            <p className="text-xs capitalize text-[#9CA3AF]">{stage.status}</p>
          </div>
        </div>
      ))}
    </div>
  )
}

export { PriorityScoreCard, TrustScoreCard, ConfidenceMeter, RiskIndicator, PipelineStatus }
export type { PriorityCardProps, TrustCardProps, PipelineStage, PipelineStageStatus }
