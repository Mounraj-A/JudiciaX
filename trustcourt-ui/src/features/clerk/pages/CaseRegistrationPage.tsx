// ─── CaseRegistrationPage — Phase F8 ──────────────────────────────────────────
import { useParams, useNavigate } from 'react-router-dom'
import { PageHeader } from '@/shared/components/layout'
import { Input, Select, Checkbox } from '@/shared/components/form'
import { ROUTES } from '@/constants/routes'

export function CaseRegistrationPage() {
  const { id } = useParams()
  const navigate = useNavigate()

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Case Registration" 
        description={`Formalize submission ${id} into the Judicial Registry.`}
      />

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
        <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <h3 style={{ margin: 0, fontSize: '1.25rem' }}>Registration Details</h3>
          
          <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
            <div style={{ flex: 1 }}>
              <Input label="Proposed Case Number" value="CS/2026/0145" readOnly />
            </div>
            <button style={{ padding: '0.625rem 1rem', background: '#F3F4F6', border: '1px solid #D1D5DB', borderRadius: '8px', cursor: 'pointer', height: '42px', fontWeight: 500 }}>
              Auto-Generate
            </button>
          </div>

          <Select 
            label="Assign Court"
            options={[{ label: 'High Court - Courtroom 1', value: 'hc1' }, { label: 'High Court - Courtroom 2', value: 'hc2' }]}
          />

          <Select 
            label="Bench Assignment (Optional)"
            options={[{ label: 'Hon. Justice A. Patel', value: 'j1' }, { label: 'Hon. Justice S. Kumar', value: 'j2' }]}
          />

          <Input label="Registration Remarks (Internal)" type="text" />

        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h3 style={{ margin: '0 0 1rem 0', fontSize: '1.25rem' }}>Pre-requisites Check</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Checkbox label="All documents verified" defaultChecked disabled />
              <Checkbox label="Court fees realized" defaultChecked disabled />
              <Checkbox label="Procedural scrutiny passed" defaultChecked disabled />
            </div>
          </div>

          <div style={{ background: '#F9FAFB', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <h3 style={{ margin: 0, fontSize: '1.125rem', color: '#111827' }}>Finalize Registration</h3>
            <p style={{ margin: 0, fontSize: '0.875rem', color: '#6B7280' }}>
              Registering this case will permanently assign the Case Number and notify the Advocate. It will then be moved to the scheduling queue.
            </p>
            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
              <button 
                onClick={() => navigate(ROUTES.CLERK.HEARINGS)}
                style={{ flex: 1, padding: '0.75rem', background: '#10B981', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}
              >
                Register Case
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
