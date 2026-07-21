// ─── SearchInput — Phase F3 ───────────────────────────────────────────────────
import React, { useRef, useState } from 'react'

interface SearchInputProps {
  value:       string
  onChange:    (val: string) => void
  placeholder?:string
  onClear?:    () => void
  onFocus?:    () => void
  onBlur?:     () => void
  history?:    string[]
  onSelectHistory?: (val: string) => void
  disabled?:   boolean
  className?:  string
}

export function SearchInput({
  value, onChange, placeholder = 'Search...', onClear, onFocus, onBlur,
  history = [], onSelectHistory, disabled = false, className = ''
}: SearchInputProps) {
  const [showHistory, setShowHistory] = useState(false)
  const containerRef                  = useRef<HTMLDivElement>(null)

  const handleFocus = () => { setShowHistory(true); onFocus?.() }
  const handleBlur  = (e: React.FocusEvent) => {
    if (!containerRef.current?.contains(e.relatedTarget as Node)) {
      setShowHistory(false)
      onBlur?.()
    }
  }

  return (
    <div ref={containerRef} className={className} style={{ position: 'relative', width: '100%' }} onBlur={handleBlur}>
      <div style={{
        display: 'flex', alignItems: 'center', background: '#FFFFFF',
        border: '1px solid #D1D5DB', borderRadius: '0.5rem',
        padding: '0.375rem 0.75rem', gap: '0.5rem',
        transition: 'border-color 0.15s, box-shadow 0.15s',
        opacity: disabled ? 0.6 : 1, pointerEvents: disabled ? 'none' : 'auto',
      }}>
        <span style={{ color: '#9CA3AF', fontSize: '1.125rem' }}>⚲</span>
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          onFocus={handleFocus}
          placeholder={placeholder}
          disabled={disabled}
          style={{
            flex: 1, border: 'none', background: 'transparent', outline: 'none',
            fontSize: '0.875rem', color: '#111827', minWidth: 0,
          }}
        />
        {value && (
          <button
            type="button"
            onClick={() => { onChange(''); onClear?.(); }}
            style={{
              background: 'none', border: 'none', color: '#9CA3AF', cursor: 'pointer',
              padding: '0.125rem', display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}
          >
            ✕
          </button>
        )}
      </div>

      {showHistory && history.length > 0 && (
        <div style={{
          position: 'absolute', top: '100%', left: 0, right: 0, marginTop: '0.25rem',
          background: '#FFFFFF', border: '1px solid #E5E7EB', borderRadius: '0.5rem',
          boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)', zIndex: 50, overflow: 'hidden'
        }}>
          {history.map((h, i) => (
            <button
              key={i}
              type="button"
              onClick={() => { onSelectHistory?.(h); setShowHistory(false) }}
              style={{
                display: 'flex', alignItems: 'center', gap: '0.5rem', width: '100%',
                padding: '0.5rem 0.75rem', border: 'none', background: 'none',
                textAlign: 'left', fontSize: '0.8125rem', color: '#374151', cursor: 'pointer',
              }}
              onMouseEnter={(e) => ((e.target as HTMLElement).style.background = '#F9FAFB')}
              onMouseLeave={(e) => ((e.target as HTMLElement).style.background = 'none')}
            >
              <span style={{ color: '#9CA3AF' }}>↺</span>
              {h}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
