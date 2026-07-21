// ─── Admin Table Component ────────────────────────────────────────────────────
import React, { useState } from 'react'
import { DataGrid } from '@/shared/components/datagrid/DataGrid'
import { Button } from '@/shared/design-system/components/primitives/Button'

interface AdminTableProps {
  data: any[]
  columns: any[]
  rowId: string
  loading?: boolean
  error?: string | null
  emptyMessage?: string
  selectable?: boolean
  bulkActions?: any[]
  onRowClick?: (row: any) => void
  onRefresh?: () => void
  onExport?: () => void
  pagination?: {
    page: number
    size: number
    totalElements: number
    totalPages: number
    onPageChange: (page: number) => void
    onSizeChange: (size: number) => void
  }
}

export function AdminTable({
  data, columns, rowId, loading, error, emptyMessage,
  selectable, bulkActions, onRowClick, onRefresh, onExport, pagination
}: AdminTableProps) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
        {onRefresh && (
          <Button variant="ghost" onClick={onRefresh} disabled={loading}>
            ↻ Refresh
          </Button>
        )}
        {onExport && (
          <Button variant="secondary" onClick={onExport} disabled={loading || data.length === 0}>
            ↓ Export CSV
          </Button>
        )}
      </div>

      <DataGrid
        data={data}
        columns={columns}
        rowId={rowId}
        loading={loading}
        error={error}
        emptyMessage={emptyMessage}
        selectable={selectable}
        bulkActions={bulkActions}
        onRowClick={onRowClick}
      />

      {pagination && data.length > 0 && !loading && !error && (
        <div style={{
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          padding: '1rem', background: '#FFFFFF', border: '1px solid #E5E7EB',
          borderRadius: '0.5rem', marginTop: '0.5rem'
        }}>
          <div style={{ fontSize: '0.875rem', color: '#6B7280' }}>
            Showing {pagination.page * pagination.size + 1} to {Math.min((pagination.page + 1) * pagination.size, pagination.totalElements)} of {pagination.totalElements} results
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <select
              value={pagination.size}
              onChange={(e) => pagination.onSizeChange(Number(e.target.value))}
              style={{
                padding: '0.375rem 2rem 0.375rem 0.75rem', borderRadius: '0.375rem',
                border: '1px solid #D1D5DB', fontSize: '0.875rem', background: '#FFFFFF'
              }}
            >
              {[10, 20, 50, 100].map(sz => (
                <option key={sz} value={sz}>{sz} per page</option>
              ))}
            </select>
            <div style={{ display: 'flex', gap: '0.25rem' }}>
              <Button
                variant="ghost"
                disabled={pagination.page === 0}
                onClick={() => pagination.onPageChange(pagination.page - 1)}
              >
                Previous
              </Button>
              <Button
                variant="ghost"
                disabled={pagination.page >= pagination.totalPages - 1}
                onClick={() => pagination.onPageChange(pagination.page + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
