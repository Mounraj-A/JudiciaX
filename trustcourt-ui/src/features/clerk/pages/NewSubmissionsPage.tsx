// ─── NewSubmissionsPage — Phase F8 ──────────────────────────────────────────
import { PageHeader } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { DataGrid } from '@/shared/components/datagrid/DataGrid'
import type { ColumnDef } from '@/types/shared/table'
import { ROUTES } from '@/constants/routes'
import { Link } from 'react-router-dom'

const mockSubmissions = [
  { id: 'Sub-2026-081', type: 'Civil Suit', filedBy: 'Adv. M. Sharma', date: 'Oct 14, 2026', priority: 'Standard', status: 'Pending' },
  { id: 'Sub-2026-082', type: 'Criminal Appeal', filedBy: 'Adv. K. Singh', date: 'Oct 14, 2026', priority: 'High Priority', status: 'Pending' },
  { id: 'Sub-2026-083', type: 'Writ Petition', filedBy: 'Adv. P. Verma', date: 'Oct 13, 2026', priority: 'Standard', status: 'In Scrutiny' },
]

export function NewSubmissionsPage() {
  const columns: ColumnDef<any>[] = [
    { id: 'id', field: 'id', header: 'Submission ID', renderCell: (val: any) => <span style={{ fontWeight: 600 }}>{val}</span> },
    { id: 'type', field: 'type', header: 'Case Type' },
    { id: 'filedBy', field: 'filedBy', header: 'Advocate' },
    { id: 'date', field: 'date', header: 'Submission Date' },
    { 
      id: 'priority',
      field: 'priority', 
      header: 'Priority',
      renderCell: (val: any) => <StatusBadge status={val} />
    },
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
        <Link 
          to={ROUTES.CLERK.SCRUTINY.replace(':id', row.id)} 
          style={{ padding: '0.375rem 0.75rem', background: '#0F1D3A', color: '#FFF', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 600, textDecoration: 'none' }}
        >
          {row.status === 'In Scrutiny' ? 'Resume' : 'Begin Scrutiny'}
        </Link>
      )
    }
  ]

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="New Submissions Queue" 
        description="Filter and manage incoming case filings awaiting judicial scrutiny." 
      />

      <DataGrid
        data={mockSubmissions}
        columns={columns}
        rowId="id"
        onRowClick={(row) => console.log('View', row)}
      />
    </div>
  )
}
