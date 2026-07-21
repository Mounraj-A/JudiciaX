// ─── AssignedCasesPage — Phase F9 ───────────────────────────────────────────
import { PageHeader, ContentContainer } from '@/shared/components/layout'
import { DataGrid } from '@/shared/components/datagrid'
import type { ColumnDef } from '@/types/shared/table'
import { StatusBadge } from '@/shared/components/badges'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'

const mockCases = Array.from({ length: 15 }).map((_, i) => ({
  id: `CASE-2026-${String(1000 + i).padStart(4, '0')}`,
  title: `Petitioner ${i + 1} vs Respondent`,
  jpi: Math.floor(Math.random() * 40) + 50,
  status: ['Pending', 'Hearing Scheduled', 'Evidence Verification'][Math.floor(Math.random() * 3)],
  date: `2026-08-${String(i + 1).padStart(2, '0')}`
}))

const columns: ColumnDef<typeof mockCases[0]>[] = [
  { id: 'id', field: 'id', header: 'Case No.' },
  { id: 'title', field: 'title', header: 'Title' },
  { 
    id: 'jpi', field: 'jpi', header: 'JPI',
    renderCell: (_, row) => (
      <span className={`font-semibold ${row.jpi > 80 ? 'text-rose-600' : 'text-brand-600'}`}>
        {row.jpi}%
      </span>
    )
  },
  { 
    id: 'status', field: 'status', header: 'Status',
    renderCell: (_, row) => <StatusBadge status={row.status.includes('Hearing') ? 'success' : 'warning'} label={row.status} />
  },
  { id: 'date', field: 'date', header: 'Assigned Date' }
]

export function AssignedCasesPage() {
  const navigate = useNavigate()
  return (
    <ContentContainer>
      <PageHeader 
        title="Assigned Cases" 
        description="Cases allocated to this bench." 
      />
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl overflow-hidden">
        <DataGrid
          data={mockCases}
          columns={columns}
          rowId="id"
          onRowClick={(row) => navigate(ROUTES.JUDGE.REVIEW.replace(':id', row.id))}
        />
      </div>
    </ContentContainer>
  )
}
