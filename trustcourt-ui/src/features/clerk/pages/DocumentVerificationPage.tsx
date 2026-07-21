// ─── DocumentVerificationPage — Phase F8 ──────────────────────────────────────
import { useParams, useNavigate } from 'react-router-dom'
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { Checkbox } from '@/shared/components/form'

export function DocumentVerificationPage() {
  const { id } = useParams()
  const navigate = useNavigate()

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem', height: 'calc(100vh - 64px)' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <button 
            onClick={() => navigate(-1)}
            style={{ background: 'none', border: 'none', color: '#6B7280', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}
          >
            ← Back to Scrutiny
          </button>
          <PageHeader 
            title="Document Verification" 
            description={`Verifying documents for Case: ${id}`} 
          />
        </div>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button style={{ padding: '0.625rem 1.25rem', background: '#EF4444', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}>Mark Defective</button>
          <button style={{ padding: '0.625rem 1.25rem', background: '#10B981', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}>Approve Document</button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '2rem', flex: 1, minHeight: 0 }}>
        {/* PDF Viewer Mock */}
        <div style={{ background: '#374151', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#9CA3AF', flexDirection: 'column', gap: '1rem' }}>
          <div style={{ fontSize: '4rem' }}>📄</div>
          <div>Secure PDF Viewer Container</div>
          <div style={{ fontSize: '0.875rem' }}>Annexure_A_Contract.pdf</div>
        </div>

        {/* Verification Panel */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', overflowY: 'auto' }}>
          <div style={{ background: '#FFF', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E5E7EB' }}>
            <h3 style={{ margin: '0 0 1rem 0', fontSize: '1.125rem' }}>AI Pre-Check Analysis</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.875rem', color: '#4B5563' }}>OCR Readiness</span>
                <StatusBadge status="success" />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.875rem', color: '#4B5563' }}>Signature Detection</span>
                <StatusBadge status="success" />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.875rem', color: '#4B5563' }}>Duplicate Check</span>
                <StatusBadge status="success" />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.875rem', color: '#4B5563' }}>PII Redaction</span>
                <StatusBadge status="warning" />
              </div>
            </div>
          </div>

          <div style={{ background: '#FFF', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E5E7EB' }}>
            <h3 style={{ margin: '0 0 1rem 0', fontSize: '1.125rem' }}>Clerk Checklist</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Checkbox label="Pages are legible and numbered correctly" />
              <Checkbox label="Signatures match Vakalatnama" />
              <Checkbox label="Document matches its index description" />
              <Checkbox label="No visible tampering" />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
