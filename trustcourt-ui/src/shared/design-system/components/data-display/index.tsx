import React, { useState } from 'react'
import { motion } from 'framer-motion'
import { cn } from '@/shared/design-system/utils/cn'

// ─── Enterprise Table ─────────────────────────────────────────────────────────
export interface Column<T> {
  key: string
  header: string
  accessor: (row: T) => React.ReactNode
  sortable?: boolean
  width?: string
  align?: 'left' | 'center' | 'right'
}

interface EnterpriseTableProps<T extends { id: string | number }> {
  columns: Column<T>[]
  data: T[]
  isLoading?: boolean
  selectable?: boolean
  onSelectionChange?: (selected: T[]) => void
  onRowClick?: (row: T) => void
  onSort?: (key: string, dir: 'asc' | 'desc') => void
  emptyMessage?: string
  stickyHeader?: boolean
  pagination?: {
    page: number
    total: number
    pageSize: number
    onChange: (page: number) => void
  }
  bulkActions?: React.ReactNode
  caption?: string
}

function EnterpriseTable<T extends { id: string | number }>({
  columns,
  data,
  isLoading,
  selectable,
  onSelectionChange,
  onRowClick,
  onSort,
  emptyMessage = 'No data available',
  stickyHeader = true,
  pagination,
  bulkActions,
  caption,
}: EnterpriseTableProps<T>) {
  const [selected, setSelected] = useState<Set<string | number>>(new Set())
  const [sortKey, setSortKey] = useState<string | null>(null)
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc')

  const allSelected = data.length > 0 && data.every((r) => selected.has(r.id))

  const toggleAll = () => {
    const next = allSelected ? new Set<string | number>() : new Set(data.map((r) => r.id))
    setSelected(next)
    onSelectionChange?.(data.filter((r) => next.has(r.id)))
  }

  const toggleRow = (id: string | number) => {
    const next = new Set(selected)
    next.has(id) ? next.delete(id) : next.add(id)
    setSelected(next)
    onSelectionChange?.(data.filter((r) => next.has(r.id)))
  }

  const handleSort = (key: string) => {
    const dir = sortKey === key && sortDir === 'asc' ? 'desc' : 'asc'
    setSortKey(key)
    setSortDir(dir)
    onSort?.(key, dir)
  }

  return (
    <div className="flex flex-col border border-[#E5E7EB] rounded-lg overflow-hidden bg-white shadow-soft">
      {/* Bulk action bar */}
      {selectable && selected.size > 0 && bulkActions && (
        <div className="flex items-center gap-3 px-4 py-2 bg-[#EFF3FB] border-b border-[#D9E3F5]">
          <span className="text-sm font-medium text-[#0F1D3A]">{selected.size} selected</span>
          {bulkActions}
        </div>
      )}

      {/* Scroll container */}
      <div className="overflow-auto">
        <table className="w-full" role="grid" aria-label={caption}>
          {caption && <caption className="sr-only">{caption}</caption>}

          {/* Header */}
          <thead className={cn(stickyHeader && 'sticky top-0 z-10', 'bg-[#F9FAFB] border-b border-[#E5E7EB]')}>
            <tr>
              {selectable && (
                <th className="w-12 px-4 py-3">
                  <input
                    type="checkbox"
                    checked={allSelected}
                    onChange={toggleAll}
                    aria-label="Select all rows"
                    className="rounded border-[#D1D5DB] text-[#0F1D3A] focus:ring-[#1E3A8A]"
                  />
                </th>
              )}
              {columns.map((col) => (
                <th
                  key={col.key}
                  scope="col"
                  style={{ width: col.width }}
                  className={cn(
                    'px-4 py-3 text-xs font-semibold text-[#374151] uppercase tracking-wide whitespace-nowrap',
                    col.align === 'right' ? 'text-right' : col.align === 'center' ? 'text-center' : 'text-left',
                    col.sortable && 'cursor-pointer hover:text-[#0F1D3A] select-none'
                  )}
                  onClick={col.sortable ? () => handleSort(col.key) : undefined}
                  aria-sort={
                    sortKey === col.key
                      ? sortDir === 'asc' ? 'ascending' : 'descending'
                      : col.sortable ? 'none' : undefined
                  }
                >
                  <span className="flex items-center gap-1">
                    {col.header}
                    {col.sortable && (
                      <span className="opacity-40" aria-hidden="true">
                        {sortKey === col.key ? (sortDir === 'asc' ? '↑' : '↓') : '↕'}
                      </span>
                    )}
                  </span>
                </th>
              ))}
            </tr>
          </thead>

          {/* Body */}
          <tbody className="divide-y divide-[#F3F4F6]">
            {isLoading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <tr key={i}>
                  {selectable && <td className="px-4 py-3" />}
                  {columns.map((col) => (
                    <td key={col.key} className="px-4 py-3">
                      <div className="h-4 rounded bg-[#E5E7EB] animate-pulse" style={{ width: '60%' }} />
                    </td>
                  ))}
                </tr>
              ))
            ) : data.length === 0 ? (
              <tr>
                <td
                  colSpan={columns.length + (selectable ? 1 : 0)}
                  className="px-4 py-16 text-center text-sm text-[#9CA3AF]"
                >
                  {emptyMessage}
                </td>
              </tr>
            ) : (
              data.map((row) => (
                <motion.tr
                  key={row.id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  onClick={() => onRowClick?.(row)}
                  className={cn(
                    'transition-colors',
                    onRowClick && 'cursor-pointer hover:bg-[#F9FAFB]',
                    selected.has(row.id) && 'bg-[#EFF3FB]'
                  )}
                >
                  {selectable && (
                    <td className="w-12 px-4 py-3">
                      <input
                        type="checkbox"
                        checked={selected.has(row.id)}
                        onChange={() => toggleRow(row.id)}
                        onClick={(e) => e.stopPropagation()}
                        aria-label={`Select row ${row.id}`}
                        className="rounded border-[#D1D5DB] text-[#0F1D3A] focus:ring-[#1E3A8A]"
                      />
                    </td>
                  )}
                  {columns.map((col) => (
                    <td
                      key={col.key}
                      className={cn(
                        'px-4 py-3 text-sm text-[#374151] whitespace-nowrap',
                        col.align === 'right' ? 'text-right' : col.align === 'center' ? 'text-center' : ''
                      )}
                    >
                      {col.accessor(row)}
                    </td>
                  ))}
                </motion.tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination footer */}
      {pagination && (
        <div className="flex items-center justify-between px-4 py-3 border-t border-[#E5E7EB] bg-[#F9FAFB]">
          <span className="text-xs text-[#6B7280]">
            {((pagination.page - 1) * pagination.pageSize) + 1}–
            {Math.min(pagination.page * pagination.pageSize, pagination.total)} of {pagination.total}
          </span>
          <nav aria-label="Table pagination" className="flex items-center gap-1">
            <button
              onClick={() => pagination.onChange(pagination.page - 1)}
              disabled={pagination.page <= 1}
              className="h-7 w-7 flex items-center justify-center rounded text-sm text-[#374151] hover:bg-[#E5E7EB] disabled:opacity-40 disabled:cursor-not-allowed"
              aria-label="Previous page"
            >
              ‹
            </button>
            <span className="text-xs font-medium text-[#374151] px-2">
              Page {pagination.page} of {Math.ceil(pagination.total / pagination.pageSize)}
            </span>
            <button
              onClick={() => pagination.onChange(pagination.page + 1)}
              disabled={pagination.page >= Math.ceil(pagination.total / pagination.pageSize)}
              className="h-7 w-7 flex items-center justify-center rounded text-sm text-[#374151] hover:bg-[#E5E7EB] disabled:opacity-40 disabled:cursor-not-allowed"
              aria-label="Next page"
            >
              ›
            </button>
          </nav>
        </div>
      )}
    </div>
  )
}

// ─── Status Timeline ──────────────────────────────────────────────────────────
interface TimelineEvent {
  id: string
  label: string
  timestamp: string
  status: 'completed' | 'current' | 'upcoming' | 'failed'
  description?: string
  actor?: string
}

function StatusTimeline({ events }: { events: TimelineEvent[] }) {
  const statusColors = {
    completed: { dot: 'bg-[#059669]', line: 'bg-[#059669]' },
    current:   { dot: 'bg-[#1E3A8A] animate-pulse-soft', line: 'bg-[#E5E7EB]' },
    upcoming:  { dot: 'bg-[#E5E7EB]', line: 'bg-[#E5E7EB]' },
    failed:    { dot: 'bg-[#B91C1C]', line: 'bg-[#B91C1C]' },
  }

  return (
    <ol className="space-y-0" aria-label="Status timeline">
      {events.map((event, i) => {
        const sc = statusColors[event.status]
        return (
          <li key={event.id} className="flex gap-4" role="listitem">
            <div className="flex flex-col items-center">
              <div className={cn('h-3 w-3 rounded-full mt-1 shrink-0', sc.dot)} aria-hidden="true" />
              {i < events.length - 1 && (
                <div className={cn('w-0.5 flex-1 mt-1', sc.line)} aria-hidden="true" />
              )}
            </div>
            <div className={cn('pb-5', i === events.length - 1 && 'pb-0')}>
              <p className={cn(
                'text-sm font-medium',
                event.status === 'upcoming' ? 'text-[#9CA3AF]' : 'text-[#111827]'
              )}>
                {event.label}
              </p>
              {event.description && (
                <p className="text-xs text-[#6B7280] mt-0.5">{event.description}</p>
              )}
              <div className="flex items-center gap-2 mt-1">
                <time className="text-xs text-[#9CA3AF]">{event.timestamp}</time>
                {event.actor && (
                  <span className="text-xs text-[#9CA3AF]">· {event.actor}</span>
                )}
              </div>
            </div>
          </li>
        )
      })}
    </ol>
  )
}

export { EnterpriseTable, StatusTimeline }
export type { TimelineEvent }
