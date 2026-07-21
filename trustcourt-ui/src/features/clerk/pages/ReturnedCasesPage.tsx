// ─── ReturnedCasesPage — Phase F8 ───────────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { DataGrid } from '@/shared/components/datagrid/DataGrid'
import type { ColumnDef } from '@/types/shared/table'
import { ROUTES } from '@/constants/routes'
import { Link } from 'react-router-dom'

const mockReturned = [
  { id: 'Sub-2026-061', type: 'Civil Suit', filedBy: 'Adv. R. Das', returnDate: 'Oct 10, 2026', reason: 'Vakalatnama unsigned', status: 'Awaiting Resubmission' },
  { id: 'Sub-2026-052', type: 'Criminal Appeal', filedBy: 'Adv. M. Sharma', returnDate: 'Oct 09, 2026', reason: 'Insufficient court fees', status: 'Resubmitted' },
]

export function ReturnedCasesPage() {
  const columns: ColumnDef<any>[] = [
    { id: 'id', field: 'id', header: 'Submission ID', renderCell: (val: any) => <span style={{ fontWeight: 600 }}>{val}</span> },
    { id: 'type', field: 'type', header: 'Case Type' },
    { id: 'filedBy', field: 'filedBy', header: 'Advocate' },
    { id: 'returnDate', field: 'returnDate', header: 'Returned On' },
    { id: 'reason', field: 'reason', header: 'Reason for Return', renderCell: (val: any) => <span style={{ color: '#EF4444' }}>{val}</span> },
    { 
      id: 'status',
      field: 'status', 
      header: 'Status',
      renderCell: (val: any) => <StatusBadge status={val} />
    },
    {
      id: 'actions',
      header: 'Actions',
      renderCell: (_, row: any) => (
        row.status === 'Resubmitted' ? (
          <Link 
            to={ROUTES.CLERK.SCRUTINY.replace(':id', row.id)} 
            style={{ padding: '0.375rem 0.75rem', background: '#0F1D3A', color: '#FFF', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, textDecoration: 'none' }}
          >
            Review Resubmission
          </Link>
        ) : (
          <button style={{ padding: '0.375rem 0.75rem', background: '#F3F4F6', color: '#6B7280', border: '1px solid #D1D5DB', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, cursor: 'not-allowed' }}>
            Awaiting
          </button>
        )
      )
    }
  ]

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="Returned Cases" 
        description="Monitor submissions returned for procedural defects and track their resubmission." 
      />

      <DataGrid
        data={mockReturned}
        columns={columns}
        rowId="id"
      />
    </div>
  )
}
