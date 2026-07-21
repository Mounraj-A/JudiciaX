// â”€â”€â”€ Status & Entity Badges â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

// â”€â”€â”€ Base badge primitive â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
function BaseBadge({ label, color, bg, dot = false, className = '' }: {
  label: string; color: string; bg: string; dot?: boolean; className?: string
}) {
  return (
    <span className={className} style={{
      display: 'inline-flex', alignItems: 'center', gap: '0.3rem',
      padding: '0.2rem 0.55rem', borderRadius: '9999px',
      fontSize: '0.6875rem', fontWeight: 600, letterSpacing: '0.03em',
      color, background: bg, whiteSpace: 'nowrap',
    }}>
      {dot && <span style={{ width: 6, height: 6, borderRadius: '50%', background: color, flexShrink: 0 }} />}
      {label}
    </span>
  )
}

// â”€â”€â”€ StatusBadge â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
type StatusValue = 'active' | 'inactive' | 'pending' | 'suspended' | 'draft' | 'archived' | 'closed'
const STATUS_MAP: Record<StatusValue, { label: string; color: string; bg: string }> = {
  active:    { label: 'Active',    color: '#065F46', bg: '#D1FAE5' },
  inactive:  { label: 'Inactive',  color: '#374151', bg: '#F3F4F6' },
  pending:   { label: 'Pending',   color: '#92400E', bg: '#FEF3C7' },
  suspended: { label: 'Suspended', color: '#991B1B', bg: '#FEE2E2' },
  draft:     { label: 'Draft',     color: '#1E40AF', bg: '#DBEAFE' },
  archived:  { label: 'Archived',  color: '#6B7280', bg: '#F9FAFB' },
  closed:    { label: 'Closed',    color: '#6B7280', bg: '#F3F4F6' },
}
export function StatusBadge({ status, label, dot = true, className }: { status: StatusValue | string; label?: string; dot?: boolean; className?: string }) {
  const cfg = STATUS_MAP[status as StatusValue] ?? { label: status, color: '#374151', bg: '#F3F4F6' }
  if (label) cfg.label = label
  return <BaseBadge {...cfg} dot={dot} className={className} />
}

// â”€â”€â”€ PriorityBadge â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
type PriorityValue = 'critical' | 'high' | 'medium' | 'low'
const PRIORITY_MAP: Record<PriorityValue, { label: string; color: string; bg: string }> = {
  critical: { label: 'â— Critical', color: '#991B1B', bg: '#FEE2E2' },
  high:     { label: 'â–² High',     color: '#C2410C', bg: '#FFEDD5' },
  medium:   { label: 'â–  Medium',   color: '#92400E', bg: '#FEF3C7' },
  low:      { label: 'â–¼ Low',      color: '#1E40AF', bg: '#DBEAFE' },
}
export function PriorityBadge({ priority, className }: { priority: PriorityValue | string; className?: string }) {
  const cfg = PRIORITY_MAP[priority as PriorityValue] ?? { label: priority, color: '#374151', bg: '#F3F4F6' }
  return <BaseBadge {...cfg} className={className} />
}

// â”€â”€â”€ TrustBadge â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
type TrustValue = 'verified' | 'trusted' | 'disputed' | 'unverified' | 'flagged'
const TRUST_MAP: Record<TrustValue, { label: string; color: string; bg: string }> = {
  verified:   { label: 'âœ“ Verified',   color: '#065F46', bg: '#D1FAE5' },
  trusted:    { label: 'âœ“ Trusted',    color: '#1E40AF', bg: '#DBEAFE' },
  disputed:   { label: 'âš  Disputed',  color: '#92400E', bg: '#FEF3C7' },
  unverified: { label: '? Unverified', color: '#6B7280', bg: '#F3F4F6' },
  flagged:    { label: 'âš‘ Flagged',   color: '#991B1B', bg: '#FEE2E2' },
}
export function TrustBadge({ trust, className }: { trust: TrustValue | string; className?: string }) {
  const cfg = TRUST_MAP[trust as TrustValue] ?? { label: trust, color: '#374151', bg: '#F3F4F6' }
  return <BaseBadge {...cfg} className={className} />
}

// â”€â”€â”€ VerificationBadge â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
type VerifValue = 'verified' | 'unverified' | 'failed' | 'pending'
const VERIF_MAP: Record<VerifValue, { label: string; color: string; bg: string }> = {
  verified:   { label: 'âœ“ Verified',     color: '#065F46', bg: '#D1FAE5' },
  unverified: { label: 'â—‹ Unverified',   color: '#6B7280', bg: '#F3F4F6' },
  failed:     { label: 'âœ— Failed',       color: '#991B1B', bg: '#FEE2E2' },
  pending:    { label: 'â³ Pending',      color: '#92400E', bg: '#FEF3C7' },
}
export function VerificationBadge({ status, className }: { status: VerifValue | string; className?: string }) {
  const cfg = VERIF_MAP[status as VerifValue] ?? { label: status, color: '#374151', bg: '#F3F4F6' }
  return <BaseBadge {...cfg} className={className} />
}

// â”€â”€â”€ ApprovalBadge â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
type ApprovalValue = 'approved' | 'rejected' | 'pending' | 'review' | 'withdrawn'
const APPROVAL_MAP: Record<ApprovalValue, { label: string; color: string; bg: string }> = {
  approved:  { label: 'âœ“ Approved',   color: '#065F46', bg: '#D1FAE5' },
  rejected:  { label: 'âœ— Rejected',   color: '#991B1B', bg: '#FEE2E2' },
  pending:   { label: 'â³ Pending',    color: '#92400E', bg: '#FEF3C7' },
  review:    { label: 'â—‰ In Review',  color: '#1E40AF', bg: '#DBEAFE' },
  withdrawn: { label: 'â† Withdrawn',  color: '#6B7280', bg: '#F3F4F6' },
}
export function ApprovalBadge({ status, className }: { status: ApprovalValue | string; className?: string }) {
  const cfg = APPROVAL_MAP[status as ApprovalValue] ?? { label: status, color: '#374151', bg: '#F3F4F6' }
  return <BaseBadge {...cfg} className={className} />
}

// â”€â”€â”€ ProgressBadge â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
export function ProgressBadge({ value, showLabel = true, className }: { value: number; showLabel?: boolean; className?: string }) {
  const pct   = Math.min(100, Math.max(0, Math.round(value)))
  const color = pct >= 75 ? '#065F46' : pct >= 40 ? '#92400E' : '#1E40AF'
  const bg    = pct >= 75 ? '#D1FAE5' : pct >= 40 ? '#FEF3C7' : '#DBEAFE'
  return (
    <span className={className} style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem' }}>
      <span style={{ width: 36, height: 6, background: '#E5E7EB', borderRadius: 3, overflow: 'hidden' }}>
        <span style={{ display: 'block', height: '100%', width: `${pct}%`, background: color, borderRadius: 3 }} />
      </span>
      {showLabel && <BaseBadge label={`${pct}%`} color={color} bg={bg} />}
    </span>
  )
}

// â”€â”€â”€ EntityAvatar â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
const AVATAR_COLORS = ['#0F1D3A','#1E3A8A','#065F46','#C2410C','#7C3AED','#0E7490','#9D174D']
export function EntityAvatar({
  name, type, size = 36, src, className
}: { name: string; type?: string; size?: number; src?: string; className?: string }) {
  const initials = name.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()
  const color    = AVATAR_COLORS[(name.charCodeAt(0) + (name.charCodeAt(1) ?? 0)) % AVATAR_COLORS.length]
  return (
    <div className={className} title={`${name}${type ? ` (${type})` : ''}`} style={{
      width: size, height: size, borderRadius: '50%',
      background: src ? 'transparent' : color, overflow: 'hidden',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontSize: size * 0.38, fontWeight: 600, color: '#FFFFFF', flexShrink: 0,
      border: '2px solid rgba(255,255,255,0.15)',
    }}>
      {src ? <img src={src} alt={name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} /> : initials}
    </div>
  )
}
