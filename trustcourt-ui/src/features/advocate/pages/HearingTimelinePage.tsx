// â”€â”€â”€ HearingTimelinePage â€” Phase F7 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { useAdvocateHearings } from '@/features/advocate/api/useAdvocateHearings'

export function HearingTimelinePage() {
  const [page] = useState(0)
  const [size] = useState(20)

  const { data: hearingsData, isLoading, error } = useAdvocateHearings(page, size)
  const hearings = hearingsData?.data || []

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Hearing Timeline" 
        description="Track your scheduled court appearances, virtual links, and concluded hearings." 
      />

      <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '2rem' }}>
        <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E5E7EB' }}>
          <h2 style={{ margin: '0 0 2rem 0', fontSize: '1.25rem' }}>Upcoming & Past Schedule</h2>
          
          {isLoading ? (
            <div style={{ padding: '2rem', textAlign: 'center' }}>Loading hearings...</div>
          ) : error ? (
            <div style={{ padding: '2rem', color: 'red', textAlign: 'center' }}>Failed to load hearings.</div>
          ) : hearings.length === 0 ? (
            <div style={{ padding: '2rem', textAlign: 'center', color: '#6B7280' }}>No hearings found.</div>
          ) : (
            <div style={{ position: 'relative' }}>
              <div style={{ position: 'absolute', top: 0, bottom: 0, left: '11px', width: '2px', background: '#E5E7EB' }} />
              
              <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
                {hearings.map((h: any) => {
                  const hearingDateObj = new Date(h.hearingDate)
                  const isConcluded = h.status === 'CONCLUDED'
                  
                  return (
                    <div key={h.uuid} style={{ display: 'flex', gap: '1.5rem', position: 'relative' }}>
                      <div style={{ 
                        width: '24px', height: '24px', borderRadius: '50%', background: isConcluded ? '#E5E7EB' : '#0F1D3A', 
                        border: '4px solid #FFF', zIndex: 1, flexShrink: 0 
                      }} />
                      <div style={{ background: isConcluded ? '#F9FAFB' : '#FFFFFF', border: '1px solid #E5E7EB', borderRadius: '12px', padding: '1.5rem', flex: 1, display: 'flex', justifyContent: 'space-between', opacity: isConcluded ? 0.7 : 1 }}>
                        <div>
                          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', marginBottom: '0.5rem' }}>
                            <span style={{ fontWeight: 700, color: '#111827', fontSize: '1.125rem' }}>{hearingDateObj.toLocaleDateString()}</span>
                            <span style={{ color: '#6B7280', fontSize: '0.875rem' }}>at {hearingDateObj.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                            <StatusBadge status={h.status} />
                          </div>
                          <h3 style={{ margin: '0 0 0.5rem 0', fontSize: '1rem', color: '#1E40AF' }}>{h.caseNumber}</h3>
                          <div style={{ display: 'flex', gap: '2rem', fontSize: '0.875rem', color: '#4B5563', marginTop: '1rem' }}>
                            <div><strong>Type:</strong> {h.hearingType}</div>
                            <div><strong>Location:</strong> {h.courtRoom}</div>
                          </div>
                          {h.judgeName && (
                            <div style={{ fontSize: '0.875rem', color: '#4B5563', marginTop: '0.5rem' }}>
                              <strong>Judge:</strong> {h.judgeName}
                            </div>
                          )}
                          {h.notes && (
                            <div style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.5rem', fontStyle: 'italic' }}>
                              Note: {h.notes}
                            </div>
                          )}
                        </div>
                        {!isConcluded && (
                          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', justifyContent: 'center' }}>
                            <button style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', borderRadius: '6px', border: 'none', fontSize: '0.75rem', fontWeight: 600, cursor: 'pointer' }}>Prepare Case File</button>
                            {h.courtRoom?.toLowerCase().includes('virtual') && (
                              <button style={{ padding: '0.5rem 1rem', background: '#10B981', color: '#FFF', borderRadius: '6px', border: 'none', fontSize: '0.75rem', fontWeight: 600, cursor: 'pointer' }}>Join Call</button>
                            )}
                          </div>
                        )}
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
export default HearingTimelinePage
