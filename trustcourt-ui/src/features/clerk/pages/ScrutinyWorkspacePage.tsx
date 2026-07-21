// ─── ScrutinyWorkspacePage — Phase F8 ───────────────────────────────────────
import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { ROUTES } from '@/constants/routes'
import { Checkbox } from '@/shared/components/form'

export function ScrutinyWorkspacePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [activeTab, setActiveTab] = useState('summary')

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <PageHeader 
          title={`Scrutiny Workspace: ${id}`} 
          description="Review case details, validate documents, and complete procedural checks." 
        />
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button style={{ padding: '0.625rem 1.25rem', background: '#FFFFFF', border: '1px solid #D1D5DB', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}>Return to Advocate</button>
          <button style={{ padding: '0.625rem 1.25rem', background: '#EF4444', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}>Reject Case</button>
          <button 
            onClick={() => navigate(ROUTES.CLERK.REGISTRATION.replace(':id', id || ''))}
            style={{ padding: '0.625rem 1.25rem', background: '#10B981', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}
          >
            Accept & Register
          </button>
        </div>
      </div>

      <div style={{ borderBottom: '1px solid #E5E7EB', display: 'flex', gap: '2rem' }}>
        {['summary', 'checklist', 'documents'].map(tab => (
          <button 
            key={tab} 
            onClick={() => setActiveTab(tab)}
            style={{ 
              padding: '0.75rem 0', 
              background: 'none', 
              border: 'none', 
              borderBottom: activeTab === tab ? '2px solid #0F1D3A' : '2px solid transparent',
              color: activeTab === tab ? '#0F1D3A' : '#6B7280',
              fontWeight: activeTab === tab ? 600 : 500,
              cursor: 'pointer',
              textTransform: 'capitalize'
            }}
          >
            {tab === 'summary' ? 'Case Summary' : tab === 'checklist' ? 'Procedural Checklist' : 'Evidence Overview'}
          </button>
        ))}
      </div>

      {activeTab === 'summary' && (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h3 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Submission Details</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', fontSize: '0.875rem' }}>
              <div><span style={{ color: '#6B7280', display: 'block', marginBottom: '0.25rem' }}>Case Type</span><strong>Civil Suit</strong></div>
              <div><span style={{ color: '#6B7280', display: 'block', marginBottom: '0.25rem' }}>Jurisdiction</span><strong>High Court</strong></div>
              <div><span style={{ color: '#6B7280', display: 'block', marginBottom: '0.25rem' }}>Filing Date</span><strong>Oct 14, 2026</strong></div>
              <div><span style={{ color: '#6B7280', display: 'block', marginBottom: '0.25rem' }}>Priority</span><StatusBadge status="Standard" /></div>
            </div>
          </div>
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h3 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Parties Involved</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ padding: '1rem', background: '#F9FAFB', borderRadius: '8px' }}>
                <span style={{ color: '#6B7280', fontSize: '0.75rem', fontWeight: 600, textTransform: 'uppercase' }}>Petitioner</span>
                <div style={{ fontWeight: 600, marginTop: '0.25rem' }}>Global Tech Industries Ltd.</div>
                <div style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.25rem' }}>Rep. by Adv. M. Sharma</div>
              </div>
              <div style={{ padding: '1rem', background: '#F9FAFB', borderRadius: '8px' }}>
                <span style={{ color: '#6B7280', fontSize: '0.75rem', fontWeight: 600, textTransform: 'uppercase' }}>Respondent</span>
                <div style={{ fontWeight: 600, marginTop: '0.25rem' }}>State Government (Dept of Commerce)</div>
              </div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'checklist' && (
        <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB', maxWidth: 800 }}>
          <h3 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Validation Checklist</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <Checkbox label="Court Fees Paid (Receipt Attached)" />
            <Checkbox label="Vakalatnama correctly executed and signed" />
            <Checkbox label="Jurisdiction correctly identified" />
            <Checkbox label="Limitation period compliance verified" />
            <Checkbox label="Affidavit sworn and notarized" />
          </div>
        </div>
      )}

      {activeTab === 'documents' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {[
            { name: 'Main_Petition.pdf', pages: 45, status: 'Verified' },
            { name: 'Annexure_A_Contract.pdf', pages: 12, status: 'Pending Review' },
            { name: 'Vakalatnama.pdf', pages: 2, status: 'Verified' }
          ].map((doc, idx) => (
            <div key={idx} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '1.5rem', background: '#FFF', border: '1px solid #E5E7EB', borderRadius: '12px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <div style={{ fontSize: '2rem' }}>📄</div>
                <div>
                  <div style={{ fontWeight: 600 }}>{doc.name}</div>
                  <div style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.25rem' }}>{doc.pages} pages • OCR Ready</div>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <StatusBadge status={doc.status} />
                <button 
                  onClick={() => navigate(ROUTES.CLERK.DOCUMENTS.replace(':id', id || ''))}
                  style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', border: 'none', borderRadius: '6px', fontSize: '0.875rem', cursor: 'pointer' }}
                >
                  Verify
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
