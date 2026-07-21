// ─── PaginationBar — Phase F3 ─────────────────────────────────────────────────
import React from 'react'

interface PaginationBarProps {
  page:         number
  pageSize:     number
  totalItems:   number
  totalPages:   number
  onPageChange: (page: number) => void
  onSizeChange: (size: number) => void
  sizeOptions?: number[]
  className?:   string
}

export function PaginationBar({
  page, pageSize, totalItems, totalPages,
  onPageChange, onSizeChange, sizeOptions = [10, 20, 50, 100], className = ''
}: PaginationBarProps) {
  const first = totalItems === 0 ? 0 : (page - 1) * pageSize + 1
  const last  = Math.min(page * pageSize, totalItems)

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1).filter(
    (p) => p === 1 || p === totalPages || Math.abs(p - page) <= 1
  )
  const renderPages = () => {
    const els: React.ReactNode[] = []
    let prev = 0
    pages.forEach((p) => {
      if (prev > 0 && p - prev > 1) {
        els.push(<span key={`ell-${p}`} style={{ padding: '0 0.5rem', color: '#9CA3AF' }}>…</span>)
      }
      els.push(
        <button
          key={p}
          onClick={() => onPageChange(p)}
          disabled={p === page}
          style={{
            minWidth: '2rem', height: '2rem', padding: '0 0.5rem',
            background: p === page ? '#0F1D3A' : '#FFFFFF',
            color: p === page ? '#FFFFFF' : '#374151',
            border: p === page ? 'none' : '1px solid #D1D5DB',
            borderRadius: '0.375rem', fontSize: '0.875rem', fontWeight: p === page ? 600 : 400,
            cursor: p === page ? 'default' : 'pointer'
          }}
        >{p}</button>
      )
      prev = p
    })
    return els
  }

  return (
    <div className={className} style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '0.75rem 1rem', borderTop: '1px solid #E5E7EB', flexWrap: 'wrap', gap: '1rem',
      background: '#FFFFFF'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <span style={{ fontSize: '0.875rem', color: '#6B7280' }}>
          Showing <span style={{ fontWeight: 600, color: '#111827' }}>{first}</span> to <span style={{ fontWeight: 600, color: '#111827' }}>{last}</span> of <span style={{ fontWeight: 600, color: '#111827' }}>{totalItems}</span>
        </span>
        <select
          value={pageSize}
          onChange={(e) => onSizeChange(Number(e.target.value))}
          style={{
            padding: '0.25rem 0.5rem', fontSize: '0.875rem', border: '1px solid #D1D5DB',
            borderRadius: '0.375rem', background: '#FFFFFF', cursor: 'pointer'
          }}
        >
          {sizeOptions.map((opt) => <option key={opt} value={opt}>{opt} per page</option>)}
        </select>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <button
          onClick={() => onPageChange(page - 1)} disabled={page <= 1}
          style={{
            height: '2rem', padding: '0 0.75rem', background: '#FFFFFF',
            border: '1px solid #D1D5DB', borderRadius: '0.375rem', fontSize: '0.875rem',
            color: page <= 1 ? '#9CA3AF' : '#374151', cursor: page <= 1 ? 'not-allowed' : 'pointer'
          }}
        >Prev</button>
        {renderPages()}
        <button
          onClick={() => onPageChange(page + 1)} disabled={page >= totalPages}
          style={{
            height: '2rem', padding: '0 0.75rem', background: '#FFFFFF',
            border: '1px solid #D1D5DB', borderRadius: '0.375rem', fontSize: '0.875rem',
            color: page >= totalPages ? '#9CA3AF' : '#374151', cursor: page >= totalPages ? 'not-allowed' : 'pointer'
          }}
        >Next</button>
      </div>
    </div>
  )
}
