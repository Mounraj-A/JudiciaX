// ─── HearingSchedulingPage — Phase F8 ───────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { DataGrid } from '@/shared/components/datagrid/DataGrid'
import type { ColumnDef } from '@/types/shared/table'
import { Select, Input } from '@/shared/components/form'

const mockSchedules = [
  { caseId: 'CS/2026/0145', title: 'Global Tech v. State', date: 'Oct 20, 2026', time: '10:30 AM', court: 'Courtroom 1', status: 'Scheduled' },
  { caseId: 'CR/2026/0092', title: 'State v. Sharma', date: 'Oct 20, 2026', time: '11:45 AM', court: 'Courtroom 2', status: 'Pending Assignment' },
]

export function HearingSchedulingPage() {
  const columns: ColumnDef<any>[] = [
    { id: 'caseId', field: 'caseId', header: 'Case No.', renderCell: (val: any) => <span style={{ fontWeight: 600 }}>{val}</span> },
    { id: 'title', field: 'title', header: 'Title' },
    { id: 'date', field: 'date', header: 'Date' },
    { id: 'time', field: 'time', header: 'Time' },
    { id: 'court', field: 'court', header: 'Courtroom' },
    { 
      id: 'status',
      field: 'status', 
      header: 'Status',
      renderCell: (val: any) => <StatusBadge status={val} />
    },
    {
      id: 'actions',
      header: 'Actions',
      renderCell: () => (
        <button style={{ padding: '0.25rem 0.75rem', background: '#F3F4F6', color: '#111827', border: '1px solid #D1D5DB', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, cursor: 'pointer' }}>
          Reschedule
        </button>
      )
    }
  ]

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Hearing Scheduling" 
        description="Manage court dockets and schedule hearings for registered cases."
      />

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 3fr', gap: '2rem' }}>
        {/* Scheduling Form Sidebar */}
        <div style={{ background: '#FFF', padding: '1.5rem', borderRadius: '12px', border: '1px solid #E5E7EB', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <h3 style={{ margin: 0, fontSize: '1.125rem' }}>Schedule New Hearing</h3>
          
          <Select 
            label="Select Case"
            options={[{ label: 'CR/2026/0092 (State v. Sharma)', value: '1' }]}
          />
          <Input label="Date" type="date" />
          <Input label="Time" type="time" />
          <Select 
            label="Courtroom"
            options={[{ label: 'Courtroom 1', value: '1' }, { label: 'Courtroom 2', value: '2' }]}
          />
          <Select 
            label="Hearing Type"
            options={[{ label: 'First Hearing', value: '1' }, { label: 'Evidence', value: '2' }, { label: 'Arguments', value: '3' }]}
          />

          <button style={{ padding: '0.75rem', background: '#0F1D3A', color: '#FFF', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer', marginTop: '1rem' }}>
            Add to Docket
          </button>
        </div>

        {/* Calendar / Grid View */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', gap: '1rem' }}>
            <button style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', border: 'none', borderRadius: '8px', fontSize: '0.875rem', cursor: 'pointer' }}>Today</button>
            <button style={{ padding: '0.5rem 1rem', background: '#F3F4F6', color: '#111827', border: '1px solid #D1D5DB', borderRadius: '8px', fontSize: '0.875rem', cursor: 'pointer' }}>Tomorrow</button>
            <button style={{ padding: '0.5rem 1rem', background: '#F3F4F6', color: '#111827', border: '1px solid #D1D5DB', borderRadius: '8px', fontSize: '0.875rem', cursor: 'pointer' }}>This Week</button>
          </div>

          <DataGrid
            data={mockSchedules}
            columns={columns}
            rowId="caseId"
          />
        </div>
      </div>
    </div>
  )
}
