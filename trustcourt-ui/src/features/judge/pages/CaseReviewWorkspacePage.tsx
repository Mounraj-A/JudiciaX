// ─── CaseReviewWorkspacePage — Phase F9 ─────────────────────────────────────
import { useParams, useNavigate } from 'react-router-dom'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { ROUTES } from '@/constants/routes'
import { 
  AIDecisionSupportPanel, 
  ExplainableAIPanel, 
  JudicialNotesPanel, 
  DecisionActionPanel 
} from '../components'

export function CaseReviewWorkspacePage() {
  const { id } = useParams()
  const navigate = useNavigate()

  return (
    <ContentContainer maxWidth="1600px">
      <div className="flex justify-between items-start mb-6">
        <PageHeader 
          title={`Review Workspace: ${id || 'Unknown Case'}`}
          description="Primary judicial workspace. Review case details, AI insights, and finalize decisions."
        />
        <div className="flex gap-2">
          <Button variant="outline" onClick={() => navigate(ROUTES.JUDGE.DOCUMENT.replace(':id', id || ''))}>Open Documents</Button>
          <Button variant="outline" onClick={() => navigate(ROUTES.JUDGE.EVIDENCE.replace(':id', id || ''))}>Open Evidence</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column - Case Details & AI */}
        <div className="lg:col-span-8 flex flex-col gap-6">
          <Section title="Case Summary" bordered padded>
            <div className="flex items-center gap-4 mb-4 border-b border-slate-200 dark:border-slate-700 pb-4">
              <StatusBadge status="warning" label="Pending Decision" />
              <StatusBadge status="info" label="Priority: High" />
              <span className="text-sm font-medium text-slate-500">Filed: 2026-06-15</span>
            </div>
            <div className="text-sm text-slate-700 dark:text-slate-300 space-y-2">
              <p><strong>Title:</strong> STATE vs. DEFENDANT</p>
              <p><strong>Counsel for Petitioner:</strong> Alice Advocate</p>
              <p><strong>Counsel for Respondent:</strong> Bob Barrister</p>
              <p><strong>Synopsis:</strong> The petitioner challenges the recent zoning ordinance amendment passed by the municipal corporation...</p>
            </div>
          </Section>

          <AIDecisionSupportPanel />
          <ExplainableAIPanel />
        </div>

        {/* Right Column - Actions & Notes */}
        <div className="lg:col-span-4 flex flex-col gap-6">
          <DecisionActionPanel />
          <JudicialNotesPanel />
        </div>
      </div>
    </ContentContainer>
  )
}
