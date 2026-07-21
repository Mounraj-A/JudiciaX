// ─── ExplainableAIPanel — Phase F9 ───────────────────────────────────────────
import { Section } from '@/shared/components/layout'
import { FeatureImportanceChart } from './FeatureImportanceChart'
import { TrustRadar } from './TrustRadar'

const MOCK_FEATURES = [
  { name: 'Document Consistency', score: 0.95, color: 'bg-emerald-500' },
  { name: 'Precedent Alignment', score: 0.88, color: 'bg-brand-500' },
  { name: 'Timeline Continuity', score: 0.76, color: 'bg-amber-500' },
  { name: 'Evidence Completeness', score: 0.65, color: 'bg-rose-500' },
]

const MOCK_RADAR = {
  labels: ['Veracity', 'Completeness', 'Timeliness', 'Consistency', 'Relevance'],
  scores: [95, 78, 88, 92, 85],
}

export function ExplainableAIPanel() {
  return (
    <Section title="Explainable AI (XAI)" subtitle="Model reasoning & trust factors" bordered padded>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <FeatureImportanceChart features={MOCK_FEATURES} />
        <TrustRadar labels={MOCK_RADAR.labels} scores={MOCK_RADAR.scores} />
      </div>
      <div className="mt-4 p-4 bg-slate-50 dark:bg-slate-800/50 rounded-md border border-slate-200 dark:border-slate-700">
        <h4 className="text-sm font-semibold mb-2">Reasoning Summary</h4>
        <p className="text-sm text-slate-600 dark:text-slate-400">
          The Judicial Priority Index is elevated due to a pending statutory deadline identified in the procedural history. 
          The Trust Score is high (92%) indicating strong consistency across submitted affidavits, though evidence completeness 
          remains a minor concern (65%).
        </p>
      </div>
    </Section>
  )
}
