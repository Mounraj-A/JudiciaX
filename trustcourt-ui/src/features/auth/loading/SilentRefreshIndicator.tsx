// ─── Silent Refresh Indicator ─────────────────────────────────────────────────
// Phase F2 – Non-blocking indicator when isRefreshing is true
// Phase F2 – Non-blocking indicator when isRefreshing is true
import { useAppSelector } from '@/store'
import { selectIsRefreshing } from '@/store/slices/authSlice'

export function SilentRefreshIndicator() {
  const isRefreshing = useAppSelector(selectIsRefreshing)

  if (!isRefreshing) return null

  return (
    <div
      role="status"
      aria-label="Refreshing session"
      title="Refreshing session..."
      style={{
        position:     'fixed',
        bottom:       16,
        right:        16,
        padding:      '6px 12px',
        background:   'rgba(124,58,237,0.15)',
        border:       '1px solid rgba(124,58,237,0.3)',
        borderRadius: 20,
        display:      'flex',
        alignItems:   'center',
        gap:          8,
        zIndex:       9000,
        backdropFilter: 'blur(8px)',
      }}
    >
      <div style={{
        width:          10,
        height:         10,
        borderRadius:   '50%',
        border:         '2px solid rgba(124,58,237,0.3)',
        borderTopColor: '#7C3AED',
        animation:      'spin 0.8s linear infinite',
      }} />
      <span style={{ color: '#94A3B8', fontSize: 11, fontFamily: 'Inter, sans-serif' }}>
        Refreshing session
      </span>
    </div>
  )
}
