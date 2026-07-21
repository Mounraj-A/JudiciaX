// ─── RejectedCasesPage — Phase F8 ───────────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { DataGrid } from '@/shared/components/datagrid/DataGrid'
import type { ColumnDef } from '@/types/shared/table'

const mockRejected = [
  { id: 'Sub-2026-031', type: 'Writ Petition', filedBy: 'Adv. L. Kapoor', rejectDate: 'Oct 01, 2026', reason: 'Time barred by limitation act', status: 'Permanently Rejected' },
  { id: 'Sub-2026-018', type: 'Civil Suit', filedBy: 'Adv. S. Nair', rejectDate: 'Sep 25, 2026', reason: 'Lack of jurisdiction', status: 'Permanently Rejected' },
]

export function RejectedCasesPage() {
  const columns: ColumnDef<any>[] = [
    { id: 'id', field: 'id', header: 'Submission ID', renderCell: (val: any) => <span style={{ fontWeight: 600 }}>{val}</span> },
    { id: 'type', field: 'type', header: 'Case Type' },
    { id: 'filedBy', field: 'filedBy', header: 'Advocate' },
    { id: 'rejectDate', field: 'rejectDate', header: 'Rejected On' },
    { id: 'reason', field: 'reason', header: 'Primary Reason', renderCell: (val: any) => <span style={{ color: '#EF4444' }}>{val}</span> },
    { 
      id: 'status',
      field: 'status', 
      header: 'Status',
      renderCell: () => <StatusBadge status="error" />
    },
    {
      id: 'actions',
      header: 'Actions',
      renderCell: () => (
        <button style={{ padding: '0.375rem 0.75rem', background: '#FFFFFF', color: '#111827', border: '1px solid #D1D5DB', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, cursor: 'pointer' }}>
          View Notes
        </button>
      )
    }
  ]

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Rejected Cases" 
        description="Archive of submissions permanently rejected during the scrutiny process." 
      />

      <DataGrid
        data={mockRejected}
        columns={columns}
        rowId="id"
      />
    </div>
  )
}
