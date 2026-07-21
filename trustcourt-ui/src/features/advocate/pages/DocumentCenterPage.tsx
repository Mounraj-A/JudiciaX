// â”€â”€â”€ DocumentCenterPage â€” Phase F7 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { PageHeader } from '@/shared/components/layout'
import { Link } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'

export function DocumentCenterPage() {
  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Document Center" 
        description="Global document management is currently under development." 
      />

      <div style={{ textAlign: 'center', padding: '6rem 2rem', background: '#FFFFFF', borderRadius: '16px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{ fontSize: '3rem', marginBottom: '1.5rem', opacity: 0.8 }}>ðŸ“</div>
        <h2 style={{ margin: '0 0 0.75rem 0', fontSize: '1.5rem', color: '#111827' }}>Document Center</h2>
        <p style={{ margin: '0 0 2rem 0', color: '#6B7280', maxWidth: '500px', lineHeight: '1.6' }}>
          The global Document Center module is ready. Backend services for cross-case document management are currently under development.
        </p>
        
        <div style={{ display: 'inline-block', background: '#FEF3C7', color: '#D97706', padding: '0.375rem 0.75rem', borderRadius: '9999px', fontSize: '0.75rem', fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase', marginBottom: '2rem' }}>
          Status: Backend Integration Pending
        </div>

        <div style={{ padding: '1.5rem', background: '#F9FAFB', borderRadius: '12px', border: '1px solid #E5E7EB', maxWidth: '600px', width: '100%', textAlign: 'left' }}>
          <h4 style={{ margin: '0 0 1rem 0', color: '#374151', fontSize: '0.875rem' }}>How to manage documents right now:</h4>
          <ul style={{ margin: 0, paddingLeft: '1.5rem', color: '#4B5563', fontSize: '0.875rem', lineHeight: '1.8' }}>
            <li>Navigate to <Link to={ROUTES.ADVOCATE.CASES} style={{ color: '#0F1D3A', fontWeight: 600 }}>My Cases</Link></li>
            <li>Select a specific case</li>
            <li>Use the Documents tab within the Case Details page to upload and verify case-specific files</li>
          </ul>
        </div>
      </div>
    </div>
  )
}
export default DocumentCenterPage
