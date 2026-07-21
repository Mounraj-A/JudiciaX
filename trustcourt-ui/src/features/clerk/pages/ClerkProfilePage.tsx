// ─── ClerkProfilePage — Phase F8 ──────────────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { Input } from '@/shared/components/form'
import { EntityAvatar } from '@/shared/components/badges'
import { useAuthContext } from '@/features/auth/contexts/AuthContext'

export function ClerkProfilePage() {
  const { logout } = useAuthContext()
  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Clerk Profile & Settings" 
        description="Manage your system preferences and court assignment details." 
        actions={<button style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', borderRadius: '8px', border: 'none', fontSize: '0.875rem', fontWeight: 600, cursor: 'pointer' }}>Save Changes</button>}
      />

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '2rem' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Personal Information</h2>
            
            <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' }}>
              <EntityAvatar type="individual" name="C" size={64} />
              <div>
                <button style={{ padding: '0.5rem 1rem', background: '#F3F4F6', border: '1px solid #D1D5DB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 500, cursor: 'pointer' }}>Change Photo</button>
              </div>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
                <Input label="First Name" defaultValue="Vikram" />
                <Input label="Last Name" defaultValue="Singh" />
              </div>
              <Input label="Email Address" defaultValue="clerk.vsingh@registry.court.gov" readOnly />
              <Input label="Clerk ID Number" defaultValue="CLK-99342" readOnly />
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Court Assignment</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              <Input label="Assigned Jurisdiction" defaultValue="High Court Registry" readOnly />
              <Input label="Designation" defaultValue="Senior Scrutiny Clerk" readOnly />
              <div style={{ padding: '1rem', background: '#F3F4F6', borderRadius: '8px', fontSize: '0.875rem', color: '#4B5563' }}>
                Reassignments require Administrator approval. Please contact the Registry Master for changes.
              </div>
            </div>
          </div>

          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
            <h2 style={{ margin: '0 0 1.5rem 0', fontSize: '1.25rem' }}>Security Settings</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <button style={{ textAlign: 'left', padding: '1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '8px', cursor: 'pointer', display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontWeight: 500 }}>Change Password</span>
                <span style={{ color: '#9CA3AF' }}>→</span>
              </button>
              <button style={{ textAlign: 'left', padding: '1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '8px', cursor: 'pointer', display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ fontWeight: 500 }}>Two-Factor Authentication (2FA)</span>
                <span style={{ color: '#10B981', fontWeight: 500 }}>Enabled</span>
              </button>
              <button 
                type="button" 
                onClick={() => logout().catch(() => {})}
                style={{ textAlign: 'center', padding: '1rem', background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: '8px', cursor: 'pointer', color: '#DC2626', fontWeight: 600, marginTop: '1rem' }}>
                Sign Out
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
