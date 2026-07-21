// ─── DataGrid Core — Phase F3 ─────────────────────────────────────────────────
import React, { useMemo } from 'react'
import type { DataGridProps } from '@/types/shared/table'
import { useSelection } from '@/shared/hooks/useSelection'
import { EmptyLayout, LoadingLayout, ErrorLayout } from '../layout/StateLayouts'

export function DataGrid<TData extends Record<string, unknown>>({
  data, columns, rowId, loading, error, emptyMessage = 'No data found',
  selectable = false, bulkActions,
  onRowClick, className = ''
}: DataGridProps<TData>) {

  const allIds = useMemo(() => data.map((r) => String(r[rowId])), [data, rowId])
  const selection = useSelection(allIds)

  if (loading && data.length === 0) return <LoadingLayout />
  if (error) return <ErrorLayout message={error} />
  if (data.length === 0) return <EmptyLayout title={emptyMessage} />

  const visibleCols = columns.filter((c) => c.visible !== false)

  return (
    <div className={className} style={{
      border: '1px solid #E5E7EB', borderRadius: '0.5rem', overflow: 'hidden',
      background: '#FFFFFF', display: 'flex', flexDirection: 'column'
    }}>
      {/* Bulk Actions Bar */}
      {selectable && selection.selectedCount > 0 && bulkActions && bulkActions.length > 0 && (
        <div style={{
          background: '#F3F4F6', padding: '0.75rem 1rem', borderBottom: '1px solid #E5E7EB',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between'
        }}>
          <span style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151' }}>
            {selection.selectedCount} item(s) selected
          </span>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            {bulkActions.map((act) => (
              <button
                key={act.id}
                disabled={act.disabled?.(selection.selectedArray)}
                onClick={() => act.onClick(selection.selectedArray)}
                style={{
                  fontSize: '0.75rem', padding: '0.375rem 0.75rem', borderRadius: '0.25rem',
                  border: act.variant === 'primary' ? 'none' : '1px solid #D1D5DB',
                  background: act.variant === 'primary' ? '#0F1D3A' : act.variant === 'danger' ? '#FEF2F2' : '#FFFFFF',
                  color: act.variant === 'primary' ? '#FFFFFF' : act.variant === 'danger' ? '#DC2626' : '#374151',
                  cursor: act.disabled?.(selection.selectedArray) ? 'not-allowed' : 'pointer',
                  fontWeight: 500
                }}
              >
                {act.label}
              </button>
            ))}
          </div>
        </div>
      )}

      <div style={{ overflowX: 'auto', width: '100%' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead style={{ background: '#F9FAFB', borderBottom: '1px solid #E5E7EB' }}>
            <tr>
              {selectable && (
                <th style={{ padding: '0.75rem 1rem', width: 48, borderRight: '1px solid #E5E7EB' }}>
                  <input
                    type="checkbox"
                    checked={selection.isAllSelected}
                    ref={(el) => { if (el) el.indeterminate = selection.isIndeterminate }}
                    onChange={() => selection.selectAll(allIds)}
                  />
                </th>
              )}
              {visibleCols.map((col) => (
                <th key={col.id} style={{
                  padding: '0.75rem 1rem', fontSize: '0.75rem', fontWeight: 600, color: '#6B7280',
                  textTransform: 'uppercase', letterSpacing: '0.05em',
                  width: col.width, minWidth: col.minWidth, maxWidth: col.maxWidth,
                  textAlign: col.align ?? 'left',
                  borderRight: '1px solid #E5E7EB'
                }}>
                  {col.renderHeader ? col.renderHeader() : col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((row, idx) => {
              const id = String(row[rowId])
              const isSelected = selection.isSelected(id)
              return (
                <tr
                  key={id}
                  onClick={() => onRowClick?.(row)}
                  style={{
                    borderBottom: idx < data.length - 1 ? '1px solid #E5E7EB' : 'none',
                    background: isSelected ? '#F3F6FF' : '#FFFFFF',
                    cursor: onRowClick ? 'pointer' : 'default',
                    transition: 'background 0.15s'
                  }}
                >
                  {selectable && (
                    <td style={{ padding: '0.75rem 1rem', borderRight: '1px solid #E5E7EB' }} onClick={(e) => e.stopPropagation()}>
                      <input type="checkbox" checked={isSelected} onChange={() => selection.toggleItem(id)} />
                    </td>
                  )}
                  {visibleCols.map((col) => (
                    <td key={col.id} style={{
                      padding: '0.75rem 1rem', fontSize: '0.875rem', color: '#111827',
                      textAlign: col.align ?? 'left', borderRight: '1px solid #E5E7EB',
                      whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
                      maxWidth: col.maxWidth
                    }}>
                      {col.renderCell ? col.renderCell(col.field ? row[col.field] : undefined, row) : String(col.field ? row[col.field] ?? '' : '')}
                    </td>
                  ))}
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </div>
  )
}
