// ─── MyCasesPage — Phase F7 ───────────────────────────────────────────────────
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'
import { PageHeader } from '@/shared/components/layout'
import { DataGrid } from '@/shared/components/datagrid/DataGrid'
import { PaginationBar } from '@/shared/components/datagrid/PaginationBar'
import { StatusBadge } from '@/shared/components/badges'
import { Input } from '@/shared/components/form'
import type { ColumnDef } from '@/types/shared/table'
import { useAdvocateCases, useSearchCases } from '@/features/advocate/api/useAdvocateCases'
import { useDebounce } from '@/shared/hooks/useDebounce'

export function MyCasesPage() {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState('')
  const [page, setPage] = useState(0) // Zero indexed for backend
  const [pageSize, setPageSize] = useState(10)
  
  const debouncedSearchTerm = useDebounce(searchTerm, 500)

  // Use search if keyword exists, else use list API
  const { data: casesData, isLoading, error } = debouncedSearchTerm 
    ? useSearchCases(debouncedSearchTerm, page, pageSize)
    : useAdvocateCases(page, pageSize)

  const columns: ColumnDef<any>[] = [
    {
      id: 'caseNumber',
      header: 'Case Number',
      field: 'caseNumber',
      renderCell: (val: any) => <span style={{ fontWeight: 600, color: '#0F1D3A' }}>{val}</span>
    },
    { 
      id: 'title', 
      header: 'Title / Parties', 
      field: 'caseNumber', // fallback
      renderCell: (_, row) => `${row.petitionerName} vs ${row.respondentName}`
    },
    {
      id: 'caseCategory',
      header: 'Category',
      field: 'caseCategory'
    },
    {
      id: 'status',
      header: 'Status',
      field: 'status',
      renderCell: (val: any) => {
        return <StatusBadge status={val} />
      }
    },
    { id: 'filingDate', header: 'Filing Date', field: 'filingDate' }
  ]

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <PageHeader 
        title="My Cases & Filings" 
        description="Manage your active litigation, drafts, and submissions."
        actions={
          <div style={{ display: 'flex', gap: '1rem' }}>
            <button 
              onClick={() => {
                const url = 'http://localhost:8080/api/v1/advocate/cases/export'
                const token = localStorage.getItem('token') || ''
                fetch(url, { headers: { 'Authorization': `Bearer ${token}` } })
                  .then(res => res.blob())
                  .then(blob => {
                    const a = document.createElement('a')
                    a.href = URL.createObjectURL(blob)
                    a.download = 'my_cases.csv'
                    a.click()
                  })
              }}
              style={{ padding: '0.5rem 1rem', background: '#FFFFFF', border: '1px solid #E5E7EB', color: '#111827', borderRadius: '8px', cursor: 'pointer', fontSize: '0.875rem', fontWeight: 500 }}>
              Export CSV
            </button>
            <Link to={ROUTES.ADVOCATE.NEW_CASE} style={{ padding: '0.5rem 1rem', background: '#0F1D3A', color: '#FFF', borderRadius: '8px', textDecoration: 'none', fontSize: '0.875rem', fontWeight: 600 }}>
              + New Filing
            </Link>
          </div>
        }
      />

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ width: '320px' }}>
          <Input 
            placeholder="Search by case number or title..." 
            value={searchTerm} 
            onChange={(e) => setSearchTerm(e.target.value)} 
            rightIcon={<span style={{ color: '#9CA3AF' }}>⚲</span>}
          />
        </div>
        <button style={{ padding: '0.5rem 1rem', background: '#FFFFFF', border: '1px solid #E5E7EB', borderRadius: '8px', fontSize: '0.875rem', fontWeight: 500, cursor: 'pointer' }}>
          Advanced Filters
        </button>
      </div>

      {isLoading ? (
        <div style={{ padding: '2rem', textAlign: 'center' }}>Loading cases...</div>
      ) : error ? (
        <div style={{ padding: '2rem', color: 'red', textAlign: 'center' }}>Failed to load cases. Please try again.</div>
      ) : (
        <>
          <DataGrid
            data={casesData?.data || (casesData as any)?.content || []}
            columns={columns}
            rowId="uuid"
            selectable={true}
            onRowClick={(row: any) => navigate(ROUTES.ADVOCATE.CASE.replace(':id', row.uuid))}
            bulkActions={[
              { id: 'export', label: 'Export Selected', onClick: () => alert('Exporting...') }
            ]}
            loading={isLoading}
          />
          <PaginationBar
            page={page + 1}
            totalPages={casesData?.pagination?.totalPages || (casesData as any)?.totalPages || 1}
            pageSize={pageSize}
            totalItems={casesData?.pagination?.total || (casesData as any)?.totalElements || 0}
            onPageChange={(p) => setPage(p - 1)}
            onSizeChange={(s) => { setPageSize(s); setPage(0); }}
          />
        </>
      )}
    </div>
  )
}
export default MyCasesPage
