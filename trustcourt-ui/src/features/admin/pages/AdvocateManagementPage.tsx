import { useState } from 'react'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { AdminTable } from '../components/AdminTable'

export function AdvocateManagementPage() {
  const [showAlert, setShowAlert] = useState(true)

  // Future API Integration
  const isLoading = false
  const data: any[] = [] // Set to empty until backend is ready

  const columns = [
    { id: 'advocateId', header: 'Advocate ID', field: 'advocateId' },
    { id: 'enrollmentNumber', header: 'Enrollment Number', field: 'enrollmentNumber' },
    { id: 'fullName', header: 'Advocate Name', field: 'fullName' },
    { id: 'practiceArea', header: 'Practice Area', field: 'practiceArea' },
    { id: 'experience', header: 'Experience', field: 'experience' },
    { id: 'status', header: 'Status', field: 'status' },
    { id: 'actions', header: 'Actions', align: 'right', renderCell: () => null }
  ]

  const hasBackendSupport = false // Toggle this when API is ready

  return (
    <ContentContainer>
      <PageHeader
        title="Advocate Administration"
        subtitle="Manage registered advocates and monitor filing compliance."
      />

      {/* 6. Notification Banner */}
      {showAlert && !hasBackendSupport && (
        <Section>
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '1rem', background: '#EFF6FF', border: '1px solid #BFDBFE',
            borderRadius: '0.5rem', marginBottom: '1.5rem'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', color: '#1D4ED8' }}>
              <span style={{ fontSize: '1.25rem' }}>ℹ️</span>
              <span style={{ fontSize: '0.875rem', fontWeight: 500 }}>
                Advocate Administration backend APIs are not available in the current backend version.
              </span>
            </div>
            <button
              onClick={() => setShowAlert(false)}
              style={{ background: 'none', border: 'none', color: '#60A5FA', cursor: 'pointer', fontSize: '1.25rem', padding: '0.25rem' }}
            >
              ✕
            </button>
          </div>
        </Section>
      )}

      {/* 2. Enterprise Empty State */}
      {!hasBackendSupport && (
        <Section>
          <div style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center',
            padding: '3rem 2rem', background: '#FFFFFF', border: '1px solid #E5E7EB',
            borderRadius: '0.5rem', marginBottom: '2rem'
          }}>
            <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>⚖️</div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#111827', marginBottom: '0.5rem' }}>
              Advocate Administration
            </h2>
            <p style={{ fontSize: '1rem', color: '#4B5563', maxWidth: '600px', marginBottom: '1.5rem' }}>
              The Advocate Administration module is ready. Backend services for advocate management are currently under development.
            </p>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: '#FEF3C7', padding: '0.25rem 0.75rem', borderRadius: '9999px', border: '1px solid #FDE68A' }}>
              <span style={{ width: '8px', height: '8px', background: '#F59E0B', borderRadius: '50%' }}></span>
              <span style={{ fontSize: '0.75rem', fontWeight: 600, color: '#92400E' }}>Status : Backend Integration Pending</span>
            </div>
          </div>
        </Section>
      )}

      {/* 3. Information Card */}
      {!hasBackendSupport && (
        <Section>
          <div style={{ background: '#FFFFFF', border: '1px solid #E5E7EB', borderRadius: '0.5rem', padding: '1.5rem', marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#111827', marginBottom: '1rem' }}>This module will support:</h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1rem' }}>
              {[
                'Advocate Profile Management',
                'Enrollment Verification',
                'Practice Area Management',
                'Court Association',
                'Case Assignment Overview',
                'Performance Analytics',
                'Search & Filtering',
                'Reports'
              ].map(feature => (
                <div key={feature} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem', color: '#374151' }}>
                  <span style={{ color: '#10B981', fontWeight: 'bold' }}>✓</span> {feature}
                </div>
              ))}
            </div>
          </div>
        </Section>
      )}

      <Section>
        {/* 4. Search and Filters */}
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem', opacity: hasBackendSupport ? 1 : 0.7 }} title={hasBackendSupport ? '' : 'Available after backend integration'}>
          <input disabled={!hasBackendSupport} type="text" placeholder="Search advocates..." style={{ padding: '0.5rem', border: '1px solid #D1D5DB', borderRadius: '0.375rem', flex: 1, backgroundColor: hasBackendSupport ? '#FFFFFF' : '#F3F4F6', cursor: hasBackendSupport ? 'text' : 'not-allowed' }} />
          <select disabled={!hasBackendSupport} style={{ padding: '0.5rem', border: '1px solid #D1D5DB', borderRadius: '0.375rem', backgroundColor: hasBackendSupport ? '#FFFFFF' : '#F3F4F6', cursor: hasBackendSupport ? 'pointer' : 'not-allowed' }}><option>All Practice Areas</option></select>
          <select disabled={!hasBackendSupport} style={{ padding: '0.5rem', border: '1px solid #D1D5DB', borderRadius: '0.375rem', backgroundColor: hasBackendSupport ? '#FFFFFF' : '#F3F4F6', cursor: hasBackendSupport ? 'pointer' : 'not-allowed' }}><option>All Verification Statuses</option></select>
          <Button disabled={!hasBackendSupport} variant="outline" style={{ cursor: hasBackendSupport ? 'pointer' : 'not-allowed' }}>Refresh</Button>
        </div>

        {/* 5. Action Area */}
        {data.length > 0 || hasBackendSupport ? (
          <AdminTable
            data={data}
            columns={columns}
            rowId="uuid"
            loading={isLoading}
            emptyMessage="No advocate records found."
          />
        ) : (
          <div style={{ padding: '4rem 2rem', textAlign: 'center', background: '#F9FAFB', border: '1px dashed #D1D5DB', borderRadius: '0.5rem' }}>
            <div style={{ fontSize: '2.5rem', color: '#9CA3AF', marginBottom: '1rem' }}>📇</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 600, color: '#374151', marginBottom: '0.5rem' }}>No Advocate Records Available</div>
            <div style={{ fontSize: '0.875rem', color: '#6B7280', maxWidth: '400px', margin: '0 auto 2rem auto' }}>
              Advocate data will automatically appear once backend services become available.
            </div>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
              <Button onClick={() => window.location.reload()}>Refresh</Button>
              <Button variant="secondary" onClick={() => alert('System Status: Backend API pending deployment.')}>View System Status</Button>
            </div>
          </div>
        )}
      </Section>
    </ContentContainer>
  )
}
