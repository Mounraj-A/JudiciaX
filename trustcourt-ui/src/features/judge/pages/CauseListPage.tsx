// ─── CauseListPage — Phase F9 ───────────────────────────────────────────────
import { PageHeader, ContentContainer } from '@/shared/components/layout'
import { DataGrid } from '@/shared/components/datagrid'
import type { ColumnDef } from '@/types/shared/table'
import { StatusBadge } from '@/shared/components/badges'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'

const mockHearings = [
  { id: 'H-001', caseId: 'CASE-2026-1011', time: '10:30 AM', title: 'State vs Smith', type: 'Final Argument', status: 'Scheduled' },
  { id: 'H-002', caseId: 'CASE-2026-1012', time: '11:15 AM', title: 'Acme Corp vs John', type: 'Evidence Recording', status: 'In Progress' },
  { id: 'H-003', caseId: 'CASE-2026-1013', time: '02:00 PM', title: 'Jane vs State', type: 'Initial Hearing', status: 'Scheduled' },
]

const columns: ColumnDef<typeof mockHearings[0]>[] = [
  { id: 'time', field: 'time', header: 'Time' },
  { id: 'caseId', field: 'caseId', header: 'Case No.' },
  { id: 'title', field: 'title', header: 'Title' },
  { id: 'type', field: 'type', header: 'Hearing Type' },
  { 
    id: 'status', field: 'status', header: 'Status',
    renderCell: (_, row) => <StatusBadge status={row.status === 'In Progress' ? 'info' : 'warning'} label={row.status} />
  }
]

export function CauseListPage() {
  const navigate = useNavigate()
  return (
    <ContentContainer>
      <PageHeader 
        title="Today's Cause List" 
        description="Schedule of hearings for today." 
      />
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl overflow-hidden">
        <DataGrid
          data={mockHearings}
          columns={columns}
          rowId="id"
          onRowClick={(row) => navigate(ROUTES.JUDGE.REVIEW.replace(':id', row.caseId))}
        />
      </div>
    </ContentContainer>
  )
}
