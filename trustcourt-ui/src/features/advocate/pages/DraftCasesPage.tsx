// â”€â”€â”€ DraftCasesPage â€” Phase F7 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'
import { PageHeader } from '@/shared/components/layout'
import { Input } from '@/shared/components/form'
import { useAdvocateCasesByStatus } from '@/features/advocate/api/useAdvocateCases'

export function DraftCasesPage() {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState('')
  const [page] = useState(0)
  const [pageSize] = useState(20)

  const { data: draftsData, isLoading, error } = useAdvocateCasesByStatus('DRAFT', page, pageSize)

  const drafts = draftsData?.data || (draftsData as any)?.content || []
  const filteredDrafts = drafts.filter((d: any) => 
    (d.petitionerName && d.petitionerName.toLowerCase().includes(searchTerm.toLowerCase())) ||
    (d.caseNumber && d.caseNumber.toLowerCase().includes(searchTerm.toLowerCase()))
  )

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Draft Cases" 
        description="Resume work on your unpublished filings."
        actions={
          <Link to={ROUTES.ADVOCATE.NEW_CASE} style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', borderRadius: '8px', textDecoration: 'none', fontSize: '0.875rem', fontWeight: 600 }}>
            + Start New Draft
          </Link>
        }
      />

      <div style={{ width: '320px' }}>
        <Input 
          placeholder="Search drafts..." 
          value={searchTerm} 
          onChange={(e) => setSearchTerm(e.target.value)} 
          rightIcon={<span style={{ color: '#9CA3AF' }}>âš²</span>}
        />
      </div>

      {isLoading ? (
        <div style={{ padding: '2rem', textAlign: 'center' }}>Loading drafts...</div>
      ) : error ? (
        <div style={{ padding: '2rem', color: 'red', textAlign: 'center' }}>Failed to load drafts. Please try again.</div>
      ) : (
        <>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1.5rem' }}>
            {filteredDrafts.map((draft: any) => (
              <div key={draft.uuid} style={{ background: '#FFFFFF', borderRadius: '12px', padding: '1.5rem', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                  <h3 style={{ margin: '0 0 0.25rem 0', fontSize: '1.125rem', color: '#111827' }}>
                    {draft.petitionerName} vs {draft.respondentName}
                  </h3>
                  <p style={{ margin: 0, fontSize: '0.875rem', color: '#6B7280' }}>{draft.caseCategory}</p>
                </div>
                
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', marginBottom: '0.25rem' }}>
                    <span style={{ color: '#6B7280' }}>Completeness</span>
                    <span style={{ fontWeight: 600, color: '#111827' }}>{50}%</span>
                  </div>
                  <div style={{ width: '100%', height: '6px', background: '#F3F4F6', borderRadius: '3px', overflow: 'hidden' }}>
                    <div style={{ height: '100%', width: `50%`, background: '#F59E0B' }} />
                  </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto', paddingTop: '1rem', borderTop: '1px solid #F3F4F6' }}>
                  <span style={{ fontSize: '0.75rem', color: '#9CA3AF' }}>Started {new Date(draft.filingDate).toLocaleDateString()}</span>
                  <button 
                    onClick={() => navigate(ROUTES.ADVOCATE.CASE.replace(':id', draft.uuid))} 
                    style={{ padding: '0.375rem 0.75rem', background: '#F3F4F6', border: 'none', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, color: '#374151', cursor: 'pointer' }}
                  >
                    Resume
                  </button>
                </div>
              </div>
            ))}
          </div>

          {filteredDrafts.length === 0 && (
            <div style={{ textAlign: 'center', padding: '4rem', background: '#FFFFFF', borderRadius: '12px', border: '1px dashed #D1D5DB' }}>
              <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>ðŸ“</div>
              <h3 style={{ fontSize: '1.125rem', color: '#111827', margin: '0 0 0.5rem 0' }}>No drafts found</h3>
              <p style={{ color: '#6B7280', fontSize: '0.875rem', margin: '0 0 1.5rem 0' }}>You don't have any pending case filings.</p>
              <Link to={ROUTES.ADVOCATE.NEW_CASE} style={{ padding: '0.625rem 1.25rem', background: '#0F1D3A', color: '#FFF', borderRadius: '8px', textDecoration: 'none', fontSize: '0.875rem', fontWeight: 600 }}>Start New Case</Link>
            </div>
          )}
        </>
      )}
    </div>
  )
}
export default DraftCasesPage
