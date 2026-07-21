// ─── AIDecisionSupportPanel — Phase F9 ───────────────────────────────────────
import { Section } from '@/shared/components/layout'
import { JudicialPriorityGauge, TrustScoreGauge, ConfidenceMeter } from './AIGauges'
import { RiskMatrix } from './RiskMatrix'

const MOCK_RISKS = [
  { type: 'Statutory Deadline', level: 'high' as const, description: 'Limitation period expires in 5 days' },
  { type: 'Precedent Conflict', level: 'medium' as const, description: 'Recent Supreme Court ruling may supersede cited case law' },
  { type: 'Evidence Gap', level: 'low' as const, description: 'Missing annexure B in respondent affidavit' },
]

export function AIDecisionSupportPanel() {
  return (
    <Section title="Decision Support" subtitle="AI Insights & Workflow Analysis" bordered padded>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
        <JudicialPriorityGauge value={85} />
        <TrustScoreGauge value={92} />
        <ConfidenceMeter value={88} />
      </div>
      <div className="grid grid-cols-1 gap-4">
        <RiskMatrix risks={MOCK_RISKS} />
      </div>
    </Section>
  )
}
