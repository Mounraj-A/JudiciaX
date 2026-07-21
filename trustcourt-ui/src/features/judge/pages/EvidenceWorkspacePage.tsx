// ─── EvidenceWorkspacePage — Phase F9 ───────────────────────────────────────
import { useParams, useNavigate } from 'react-router-dom'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { ROUTES } from '@/constants/routes'

const MOCK_EVIDENCE = [
  { id: 'E-001', title: 'Property Deed', type: 'PDF', date: '2026-06-16', status: 'Verified' },
  { id: 'E-002', title: 'Site Photographs', type: 'Image', date: '2026-06-18', status: 'Pending Verification' },
  { id: 'E-003', title: 'Zoning Map', type: 'PDF', date: '2026-06-20', status: 'Verified' },
]

export function EvidenceWorkspacePage() {
  const { id } = useParams()
  const navigate = useNavigate()

  return (
    <ContentContainer>
      <div className="flex justify-between items-start mb-6">
        <PageHeader 
          title={`Evidence Workspace`}
          description={`Review and analyze evidentiary items for ${id}`}
        />
        <Button variant="outline" onClick={() => navigate(ROUTES.JUDGE.REVIEW.replace(':id', id || ''))}>
          Back to Case
        </Button>
      </div>

      <Section title="Submitted Evidence" bordered padded>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {MOCK_EVIDENCE.map((item) => (
            <div key={item.id} className="border border-slate-200 dark:border-slate-700 rounded-lg p-4 bg-white dark:bg-slate-900 flex flex-col hover:shadow-md transition-shadow cursor-pointer" onClick={() => navigate(ROUTES.JUDGE.DOCUMENT.replace(':id', item.id))}>
              <div className="flex justify-between items-start mb-2">
                <span className="text-xs font-bold text-slate-400">{item.id}</span>
                <StatusBadge status={item.status === 'Verified' ? 'success' : 'warning'} label={item.status} />
              </div>
              <h4 className="font-semibold text-slate-800 dark:text-slate-200">{item.title}</h4>
              <span className="text-xs text-slate-500 mt-1">{item.type} • {item.date}</span>
              <div className="mt-4 flex gap-2">
                <Button variant="outline" size="sm" className="w-full">Preview</Button>
              </div>
            </div>
          ))}
        </div>
      </Section>
    </ContentContainer>
  )
}
