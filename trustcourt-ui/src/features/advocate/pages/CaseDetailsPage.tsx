import React, { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { useCaseDetails, useCaseTimeline } from '@/features/advocate/api/useAdvocateCases'
import { useAdvocateOrders, useAdvocateJudgements } from '@/features/advocate/api/useAdvocateOrders'
import { useAdvocateNotes, useCreateCaseNote, useDeleteCaseNote } from '@/features/advocate/api/useAdvocateNotes'
import { FiClock, FiFileText, FiUsers, FiAward, FiBookOpen, FiArrowLeft, FiPlus, FiTrash2 } from 'react-icons/fi'

export function CaseDetailsPage() {
  const { id } = useParams()
  const [activeTab, setActiveTab] = useState('info')

  const { data: caseData, isLoading, error } = useCaseDetails(id || '')

  if (isLoading) {
    return <div style={{ padding: '4rem', textAlign: 'center', color: '#64748B' }}>Loading case details...</div>
  }

  if (error || !caseData) {
    return <div style={{ padding: '4rem', textAlign: 'center', color: '#EF4444' }}>Failed to load case details. Please try again.</div>
  }

  const tabs = [
    { id: 'info', label: 'Case Info', icon: <FiFileText /> },
    { id: 'parties', label: 'Parties', icon: <FiUsers /> },
    { id: 'timeline', label: 'Timeline', icon: <FiClock /> },
    { id: 'orders', label: 'Orders', icon: <FiBookOpen /> },
    { id: 'judgements', label: 'Judgements', icon: <FiAward /> },
    { id: 'notes', label: 'Private Notes', icon: <FiPlus /> },
  ]

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: '2rem 1rem' }}>
      <Link to={ROUTES.ADVOCATE.CASES} style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', color: '#475569', textDecoration: 'none', fontSize: '0.9rem', marginBottom: '1.5rem', fontWeight: 600, transition: 'color 0.2s' }}>
        <FiArrowLeft /> Back to My Cases
      </Link>
      
      <div style={{ background: 'linear-gradient(135deg, #0F1D3A 0%, #1E3A8A 100%)', padding: '2rem', borderRadius: '16px', color: '#FFF', boxShadow: '0 10px 15px -3px rgba(15, 29, 58, 0.3)', marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
           <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '1rem' }}>
              <StatusBadge status={caseData.status} />
              {caseData.priority === 'CRITICAL' && <span style={{ background: '#EF4444', color: '#FFF', padding: '4px 12px', borderRadius: '999px', fontSize: '0.75rem', fontWeight: 700, letterSpacing: '0.05em' }}>CRITICAL PRIORITY</span>}
           </div>
           <h1 style={{ margin: '0 0 0.5rem', fontSize: '2rem', fontWeight: 700 }}>{caseData.petitionerName} vs {caseData.respondentName}</h1>
           <p style={{ margin: 0, color: '#93C5FD', fontSize: '1rem' }}>Case Ref: {caseData.caseNumber} • Filed: {new Date(caseData.filingDate).toLocaleDateString()}</p>
        </div>
        <div style={{ display: 'flex', gap: '1rem' }}>
           <button style={{ padding: '0.75rem 1.5rem', background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px', color: '#FFF', fontWeight: 600, cursor: 'pointer', transition: 'background 0.2s' }}>Request Hearing</button>
        </div>
      </div>

      <div style={{ display: 'flex', borderBottom: '1px solid #E2E8F0', gap: '2rem', marginBottom: '2rem', overflowX: 'auto', paddingBottom: '2px' }}>
        {tabs.map(tab => (
          <button 
            key={tab.id} 
            onClick={() => setActiveTab(tab.id)}
            style={{ 
              display: 'flex', alignItems: 'center', gap: '0.5rem',
              background: 'none', border: 'none', padding: '0.75rem 0', cursor: 'pointer',
              fontSize: '0.95rem', fontWeight: 600, whiteSpace: 'nowrap',
              color: activeTab === tab.id ? '#2563EB' : '#64748B',
              borderBottom: activeTab === tab.id ? '3px solid #2563EB' : '3px solid transparent',
              transition: 'all 0.2s'
            }}
          >
            {tab.icon} {tab.label}
          </button>
        ))}
      </div>

      <div>
        {activeTab === 'info' && (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem' }}>
            <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
              <h3 style={{ margin: '0 0 1.5rem', color: '#1E293B', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><FiFileText color="#3B82F6" /> Case Overview</h3>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.75rem', borderBottom: '1px solid #F1F5F9' }}>
                   <span style={{ color: '#64748B' }}>Category</span>
                   <span style={{ fontWeight: 600, color: '#334155' }}>{caseData.caseCategory || 'N/A'}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.75rem', borderBottom: '1px solid #F1F5F9' }}>
                   <span style={{ color: '#64748B' }}>Type</span>
                   <span style={{ fontWeight: 600, color: '#334155' }}>{caseData.caseType}</span>
                </div>
                {caseData.courtRoom && (
                  <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.75rem', borderBottom: '1px solid #F1F5F9' }}>
                     <span style={{ color: '#64748B' }}>Court Room</span>
                     <span style={{ fontWeight: 600, color: '#334155' }}>{caseData.courtRoom}</span>
                  </div>
                )}
                {caseData.judgeName && (
                  <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.75rem', borderBottom: '1px solid #F1F5F9' }}>
                     <span style={{ color: '#64748B' }}>Hon\'ble Judge</span>
                     <span style={{ fontWeight: 600, color: '#334155' }}>{caseData.judgeName}</span>
                  </div>
                )}
                {caseData.nextHearingDate && (
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                     <span style={{ color: '#64748B' }}>Next Hearing</span>
                     <span style={{ fontWeight: 700, color: '#2563EB' }}>{new Date(caseData.nextHearingDate).toLocaleString()}</span>
                  </div>
                )}
              </div>
            </div>
            
            <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
              <h3 style={{ margin: '0 0 1.5rem', color: '#1E293B' }}>Description</h3>
              <p style={{ color: '#475569', lineHeight: 1.6, margin: 0 }}>
                {caseData.description || 'No detailed description provided.'}
              </p>
            </div>
          </div>
        )}

        {activeTab === 'parties' && (
          <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
            <h3 style={{ margin: '0 0 2rem', color: '#1E293B', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><FiUsers color="#3B82F6" /> Parties Involved</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '3rem' }}>
              <div>
                <h4 style={{ margin: '0 0 1rem', color: '#64748B', textTransform: 'uppercase', letterSpacing: '0.05em', fontSize: '0.85rem' }}>Petitioner(s)</h4>
                <div style={{ background: '#F8FAFC', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
                  <strong style={{ fontSize: '1.1rem', color: '#0F172A', display: 'block', marginBottom: '0.5rem' }}>{caseData.petitionerName}</strong>
                  <span style={{ color: '#64748B', fontSize: '0.9rem' }}>Primary Petitioner</span>
                </div>
              </div>
              <div>
                <h4 style={{ margin: '0 0 1rem', color: '#64748B', textTransform: 'uppercase', letterSpacing: '0.05em', fontSize: '0.85rem' }}>Respondent(s)</h4>
                <div style={{ background: '#F8FAFC', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
                  <strong style={{ fontSize: '1.1rem', color: '#0F172A', display: 'block', marginBottom: '0.5rem' }}>{caseData.respondentName}</strong>
                  <span style={{ color: '#64748B', fontSize: '0.9rem' }}>Primary Respondent</span>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'timeline' && <TimelineTab caseUuid={id || ''} />}
        {activeTab === 'orders' && <OrdersTab caseUuid={id || ''} />}
        {activeTab === 'judgements' && <JudgementsTab caseUuid={id || ''} />}
        {activeTab === 'notes' && <NotesTab caseUuid={id || ''} />}
      </div>
    </div>
  )
}

function TimelineTab({ caseUuid }: { caseUuid: string }) {
  const { data: timeline, isLoading } = useCaseTimeline(caseUuid)
  
  if (isLoading) return <div style={{ color: '#64748B', padding: '2rem', textAlign: 'center' }}>Loading timeline...</div>

  return (
    <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
      <h3 style={{ margin: '0 0 2rem', color: '#1E293B', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><FiClock color="#3B82F6" /> Audit Timeline</h3>
      <div style={{ position: 'relative', paddingLeft: '1.5rem', borderLeft: '2px solid #E2E8F0', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
         {timeline && timeline.length > 0 ? (
            timeline.map((event) => (
               <div key={event.uuid} style={{ position: 'relative' }}>
                  <div style={{ position: 'absolute', left: '-1.9rem', top: '0.2rem', width: '12px', height: '12px', borderRadius: '50%', background: '#3B82F6', border: '3px solid #FFF', boxShadow: '0 0 0 2px #BFDBFE' }}></div>
                  <div style={{ background: '#F8FAFC', padding: '1.5rem', borderRadius: '12px', border: '1px solid #F1F5F9' }}>
                     <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                        <h4 style={{ margin: 0, color: '#1E293B', fontSize: '1.05rem' }}>{event.eventTitle}</h4>
                        <span style={{ color: '#64748B', fontSize: '0.85rem' }}>{new Date(event.eventDate).toLocaleString()}</span>
                     </div>
                     <p style={{ margin: '0 0 1rem', color: '#475569', fontSize: '0.95rem' }}>{event.eventDescription}</p>
                     <div style={{ display: 'inline-block', background: '#DBEAFE', color: '#1D4ED8', padding: '4px 10px', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600 }}>
                        {event.actorName} ({event.actorRole})
                     </div>
                  </div>
               </div>
            ))
         ) : (
            <div style={{ color: '#64748B' }}>No timeline events recorded.</div>
         )}
      </div>
    </div>
  )
}

function OrdersTab({ caseUuid }: { caseUuid: string }) {
  const { data, isLoading } = useAdvocateOrders(caseUuid)

  if (isLoading) return <div style={{ color: '#64748B', padding: '2rem', textAlign: 'center' }}>Loading orders...</div>

  return (
    <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
      <h3 style={{ margin: '0 0 2rem', color: '#1E293B', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><FiBookOpen color="#3B82F6" /> Court Orders</h3>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {data?.data && data.data.length > 0 ? (
          data.data.map((order: any) => (
            <div key={order.uuid} style={{ padding: '1.5rem', border: '1px solid #E2E8F0', borderRadius: '12px', background: '#F8FAFC' }}>
              <div style={{ fontWeight: 600, fontSize: '1.1rem', color: '#0F172A' }}>{order.title}</div>
              <div style={{ fontSize: '0.85rem', color: '#64748B', marginTop: '0.5rem' }}>Dated: {order.orderDate}</div>
              <p style={{ fontSize: '0.95rem', marginTop: '1rem', color: '#334155', lineHeight: 1.6 }}>{order.orderText || 'No summary available.'}</p>
            </div>
          ))
        ) : (
          <div style={{ color: '#64748B', padding: '2rem', textAlign: 'center', background: '#F8FAFC', borderRadius: '12px', border: '1px dashed #CBD5E1' }}>No orders have been published for this case.</div>
        )}
      </div>
    </div>
  )
}

function JudgementsTab({ caseUuid }: { caseUuid: string }) {
  const { data, isLoading } = useAdvocateJudgements(caseUuid)

  if (isLoading) return <div style={{ color: '#64748B', padding: '2rem', textAlign: 'center' }}>Loading judgements...</div>

  return (
    <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
      <h3 style={{ margin: '0 0 2rem', color: '#1E293B', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><FiAward color="#3B82F6" /> Final Judgements</h3>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {data?.data && data.data.length > 0 ? (
          data.data.map((order: any) => (
            <div key={order.uuid} style={{ padding: '1.5rem', border: '1px solid #FEF3C7', borderRadius: '12px', background: '#FFFBEB' }}>
              <div style={{ fontWeight: 600, fontSize: '1.1rem', color: '#92400E' }}>{order.title}</div>
              <div style={{ fontSize: '0.85rem', color: '#B45309', marginTop: '0.5rem' }}>Dated: {order.orderDate}</div>
              <p style={{ fontSize: '0.95rem', marginTop: '1rem', color: '#78350F', lineHeight: 1.6 }}>{order.orderText || 'No summary available.'}</p>
            </div>
          ))
        ) : (
          <div style={{ color: '#64748B', padding: '2rem', textAlign: 'center', background: '#F8FAFC', borderRadius: '12px', border: '1px dashed #CBD5E1' }}>No judgements have been published.</div>
        )}
      </div>
    </div>
  )
}

function NotesTab({ caseUuid }: { caseUuid: string }) {
  const { data, isLoading } = useAdvocateNotes(caseUuid)
  const createNote = useCreateCaseNote()
  const deleteNote = useDeleteCaseNote()
  const [title, setTitle] = React.useState('')
  const [content, setContent] = React.useState('')

  if (isLoading) return <div style={{ color: '#64748B', padding: '2rem', textAlign: 'center' }}>Loading notes...</div>

  const handleCreate = () => {
    if (!title || !content) return alert('Title and content required')
    createNote.mutate({ caseUuid, request: { noteTitle: title, noteContent: content } }, {
      onSuccess: () => {
        setTitle('')
        setContent('')
      }
    })
  }

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 350px', gap: '2rem' }}>
      <div style={{ background: '#FFF', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
        <h3 style={{ margin: '0 0 2rem', color: '#1E293B', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><FiBookOpen color="#3B82F6" /> Private Case Notes</h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {data?.data && data.data.length > 0 ? (
            data.data.map((note: any) => (
              <div key={note.uuid} style={{ padding: '1.5rem', border: '1px solid #FEF9C3', borderRadius: '12px', background: '#FEFCE8', position: 'relative' }}>
                <button 
                  onClick={() => { if(confirm('Delete note?')) deleteNote.mutate({ caseUuid, noteUuid: note.uuid }) }}
                  style={{ position: 'absolute', top: '1.5rem', right: '1.5rem', background: 'none', border: 'none', color: '#EF4444', cursor: 'pointer', opacity: 0.7 }}
                  title="Delete Note"
                >
                  <FiTrash2 size={18} />
                </button>
                <div style={{ fontWeight: 600, color: '#854D0E', fontSize: '1.1rem', paddingRight: '2rem' }}>{note.noteTitle}</div>
                <div style={{ fontSize: '0.75rem', color: '#A16207', marginTop: '0.5rem' }}>
                  {new Date(note.createdAt).toLocaleString()}
                </div>
                <div style={{ fontSize: '0.95rem', marginTop: '1rem', whiteSpace: 'pre-wrap', color: '#713F12', lineHeight: 1.6 }}>
                  {note.noteContent}
                </div>
              </div>
            ))
          ) : (
            <div style={{ color: '#64748B', padding: '2rem', textAlign: 'center', background: '#F8FAFC', borderRadius: '12px', border: '1px dashed #CBD5E1' }}>No private notes yet. Use notes to keep track of hearings, arguments, and reminders.</div>
          )}
        </div>
      </div>

      <div style={{ background: '#F8FAFC', padding: '2rem', borderRadius: '16px', border: '1px solid #E2E8F0', height: 'fit-content' }}>
        <h3 style={{ margin: '0 0 1.5rem', color: '#1E293B', fontSize: '1.1rem' }}>Add New Note</h3>
        <input 
          placeholder="Note Title" 
          value={title} 
          onChange={e => setTitle(e.target.value)}
          style={{ width: '100%', padding: '0.75rem', marginBottom: '1rem', borderRadius: '8px', border: '1px solid #CBD5E1', fontFamily: 'inherit' }}
        />
        <textarea 
          placeholder="Write your note content here..." 
          value={content} 
          onChange={e => setContent(e.target.value)}
          rows={6}
          style={{ width: '100%', padding: '0.75rem', marginBottom: '1.5rem', borderRadius: '8px', border: '1px solid #CBD5E1', fontFamily: 'inherit' }}
        />
        <button 
          onClick={handleCreate}
          disabled={createNote.isPending}
          style={{ width: '100%', padding: '0.85rem', background: '#0F1D3A', color: '#FFF', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 600, transition: 'background 0.2s', opacity: createNote.isPending ? 0.7 : 1 }}
        >
          {createNote.isPending ? 'Saving...' : 'Save Private Note'}
        </button>
      </div>
    </div>
  )
}