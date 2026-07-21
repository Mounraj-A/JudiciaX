import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'

export function ClerkManagementPage() {
  return (
    <ContentContainer>
      <PageHeader
        title="Clerk Administration"
        subtitle="Manage court clerks and registry officials."
      />
      
      {/* 1. Backend Capability Panel */}
      <Section>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', padding: '1.5rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '0.5rem', marginBottom: '1.5rem' }}>
          <div>
            <h4 style={{ fontSize: '0.875rem', fontWeight: 600, color: '#111827', marginBottom: '0.75rem' }}>Available Backend Features</h4>
            <ul style={{ listStyleType: 'disc', paddingLeft: '1.5rem', fontSize: '0.875rem', color: '#10B981', display: 'flex', flexDirection: 'column', gap: '0.25rem', margin: 0 }}>
              <li>Clerk Assignment</li>
              <li>Clerk Transfer</li>
              <li>Clerk Statistics</li>
            </ul>
          </div>
          <div>
            <h4 style={{ fontSize: '0.875rem', fontWeight: 600, color: '#111827', marginBottom: '0.75rem' }}>Pending Backend Features</h4>
            <ul style={{ listStyleType: 'disc', paddingLeft: '1.5rem', fontSize: '0.875rem', color: '#6B7280', display: 'flex', flexDirection: 'column', gap: '0.25rem', margin: 0 }}>
              <li>Profile Listing</li>
              <li>Profile Details</li>
              <li>Search API</li>
              <li>Filter API</li>
              <li>Sorting API</li>
              <li>Pagination API</li>
            </ul>
          </div>
        </div>
      </Section>

      <Section>
        {/* Enterprise Controls disabled via tooltip */}
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem', opacity: 0.6 }} title="This feature will become available when the corresponding backend API is implemented.">
          <input disabled type="text" placeholder="Search clerks..." style={{ padding: '0.5rem', border: '1px solid #D1D5DB', borderRadius: '0.375rem', flex: 1, backgroundColor: '#F3F4F6' }} />
          <select disabled style={{ padding: '0.5rem', border: '1px solid #D1D5DB', borderRadius: '0.375rem', backgroundColor: '#F3F4F6' }}><option>All Courts</option></select>
          <select disabled style={{ padding: '0.5rem', border: '1px solid #D1D5DB', borderRadius: '0.375rem', backgroundColor: '#F3F4F6' }}><option>All Departments</option></select>
          <Button disabled variant="outline">Refresh</Button>
        </div>

        <div style={{ padding: '3rem 1rem', textAlign: 'center', background: '#FFFFFF', border: '1px solid #E5E7EB', borderRadius: '0.5rem' }}>
          <div style={{ fontSize: '1.125rem', fontWeight: 600, color: '#111827', marginBottom: '0.5rem' }}>Backend Integration Pending</div>
          <div style={{ fontSize: '0.875rem', color: '#6B7280', maxWidth: '400px', margin: '0 auto 1.5rem auto' }}>This module has been prepared for backend integration. Data will automatically appear once the required backend APIs are available.</div>
          <Button onClick={() => window.location.reload()}>Refresh Page</Button>
        </div>
      </Section>

      {/* Developer Notes */}
      <div style={{ marginTop: '3rem', padding: '1rem', background: '#FEF2F2', border: '1px dashed #F87171', borderRadius: '0.5rem' }}>
        <h5 style={{ margin: '0 0 0.5rem 0', color: '#991B1B', fontSize: '0.875rem' }}>[Developer Notes] Future Backend Requirements</h5>
        <code style={{ fontSize: '0.75rem', color: '#7F1D1D', display: 'block', whiteSpace: 'pre-wrap' }}>
          - GET /admin/clerks{"\n"}
          - GET /admin/clerks/{"{uuid}"}{"\n"}
          - Search, Filter, Sort, Pagination parameters on list endpoint.
        </code>
      </div>
    </ContentContainer>
  )
}
