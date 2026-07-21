// â”€â”€â”€ FilterPanel â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import type { FilterField, FilterLogic } from '@/types/shared/filter'

interface FilterPanelProps {
  filters:      FilterField[]
  logic:        FilterLogic
  onLogicChange:(logic: FilterLogic) => void
  onRemove:     (id: string) => void
  onClear:      () => void
  onAdd:        () => void
  className?:   string
}

export function FilterPanel({
  filters, logic, onLogicChange, onRemove, onClear, onAdd, className = ''
}: FilterPanelProps) {
  if (filters.length === 0) return null

  return (
    <div className={className} style={{
      background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '0.5rem',
      padding: '0.75rem 1rem', display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: '0.75rem'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', borderRight: '1px solid #E5E7EB', paddingRight: '0.75rem' }}>
        <span style={{ fontSize: '0.75rem', fontWeight: 600, color: '#6B7280' }}>MATCH</span>
        <select
          value={logic}
          onChange={(e) => onLogicChange(e.target.value as FilterLogic)}
          style={{
            fontSize: '0.75rem', padding: '0.125rem 0.25rem', borderRadius: '0.25rem',
            border: '1px solid #D1D5DB', background: '#FFFFFF', cursor: 'pointer'
          }}
        >
          <option value="AND">ALL</option>
          <option value="OR">ANY</option>
        </select>
      </div>

      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', flex: 1 }}>
        {filters.map((f) => (
          <div key={f.id} style={{
            display: 'flex', alignItems: 'center', background: '#FFFFFF',
            border: '1px solid #D1D5DB', borderRadius: '9999px',
            padding: '0.125rem 0.5rem 0.125rem 0.75rem', fontSize: '0.75rem', gap: '0.375rem'
          }}>
            <span style={{ color: '#374151', fontWeight: 500 }}>{f.field}</span>
            <span style={{ color: '#9CA3AF' }}>{f.operator}</span>
            <span style={{ color: '#111827', fontWeight: 600 }}>
              {f.value.single ?? f.value.date ?? (f.value.multi ?? f.value.range ?? f.value.dates)?.join(', ') ?? ''}
            </span>
            <button
              onClick={() => onRemove(f.id)}
              style={{
                background: 'none', border: 'none', color: '#9CA3AF', cursor: 'pointer',
                padding: '0.125rem', marginLeft: '0.25rem', display: 'flex', alignItems: 'center'
              }}
              title="Remove filter"
            >âœ•</button>
          </div>
        ))}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <button onClick={onAdd} style={{
          fontSize: '0.75rem', color: '#0F1D3A', background: 'none', border: '1px dashed #D1D5DB',
          borderRadius: '0.25rem', padding: '0.25rem 0.5rem', cursor: 'pointer', fontWeight: 500
        }}>+ Add Filter</button>
        <button onClick={onClear} style={{
          fontSize: '0.75rem', color: '#DC2626', background: 'none', border: 'none',
          cursor: 'pointer', padding: '0.25rem'
        }}>Clear</button>
      </div>
    </div>
  )
}
