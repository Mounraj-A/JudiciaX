// ─── ReservedJudgmentsPage — Phase F9 ───────────────────────────────────────
import { PageHeader, ContentContainer } from '@/shared/components/layout'
import { DataGrid } from '@/shared/components/datagrid'
import type { ColumnDef } from '@/types/shared/table'
import { StatusBadge } from '@/shared/components/badges'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'

const mockReserved = [
  { id: 'CASE-2026-0901', title: 'Tech Innovators vs Global Inc', dateReserved: '2026-07-15', deadline: '2026-08-15', status: 'Drafting' },
  { id: 'CASE-2026-0902', title: 'State vs Anderson', dateReserved: '2026-07-20', deadline: '2026-08-20', status: 'Pending Review' },
]

const columns: ColumnDef<typeof mockReserved[0]>[] = [
  { id: 'id', field: 'id', header: 'Case No.' },
  { id: 'title', field: 'title', header: 'Title' },
  { id: 'dateReserved', field: 'dateReserved', header: 'Reserved Date' },
  { id: 'deadline', field: 'deadline', header: 'Deadline' },
  { 
    id: 'status', field: 'status', header: 'Status',
    renderCell: (_, row) => <StatusBadge status="warning" label={row.status} />
  }
]

export function ReservedJudgmentsPage() {
  const navigate = useNavigate()
  return (
    <ContentContainer>
      <PageHeader 
        title="Reserved Judgments" 
        description="Cases awaiting final judgment pronouncement." 
      />
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl overflow-hidden">
        <DataGrid
          data={mockReserved}
          columns={columns}
          rowId="id"
          onRowClick={(row) => navigate(ROUTES.JUDGE.REVIEW.replace(':id', row.id))}
        />
      </div>
    </ContentContainer>
  )
}
