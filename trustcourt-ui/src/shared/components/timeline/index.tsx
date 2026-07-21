// ─── Timeline Components — Phase F3 ──────────────────────────────────────────
import React from 'react'
import { EntityAvatar } from '../badges'
import { formatRelativeTime } from '@/shared/utils/formatting'

// ─── TimelineItem — Generic event ─────────────────────────────────────────────
interface TimelineItemProps {
  icon?:      React.ReactNode
  iconColor?: string
  title:      string
  subtitle?:  string
  timestamp:  string
  last?:      boolean
  children?:  React.ReactNode
}
export function TimelineItem({ icon, iconColor = '#6B7280', title, subtitle, timestamp, last = false, children }: TimelineItemProps) {
  return (
    <div style={{ display: 'flex', gap: '0.75rem', paddingBottom: last ? 0 : '1.25rem', position: 'relative' }}>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flexShrink: 0 }}>
        <div style={{
          width: 32, height: 32, borderRadius: '50%', background: '#F3F4F6',
          border: `2px solid ${iconColor}20`, display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '0.875rem', color: iconColor,
        }}>
          {icon ?? '●'}
        </div>
        {!last && <div style={{ flex: 1, width: 2, background: '#E5E7EB', marginTop: 4 }} />}
      </div>
      <div style={{ flex: 1, paddingTop: '0.25rem', minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', gap: '0.5rem', flexWrap: 'wrap' }}>
          <span style={{ fontSize: '0.875rem', fontWeight: 500, color: '#111827' }}>{title}</span>
          <span style={{ fontSize: '0.75rem', color: '#9CA3AF', flexShrink: 0 }}>{formatRelativeTime(timestamp)}</span>
        </div>
        {subtitle && <p style={{ fontSize: '0.8125rem', color: '#6B7280', margin: '0.2rem 0 0' }}>{subtitle}</p>}
        {children && <div style={{ marginTop: '0.5rem' }}>{children}</div>}
      </div>
    </div>
  )
}

// ─── ActivityItem — With actor ────────────────────────────────────────────────
interface ActivityItemProps {
  actor:     { id: string; name: string; avatar?: string }
  action:    string
  target?:   string
  timestamp: string
  last?:     boolean
}
export function ActivityItem({ actor, action, target, timestamp, last }: ActivityItemProps) {
  return (
    <TimelineItem
      icon={<EntityAvatar name={actor.name} size={28} src={actor.avatar} />}
      title={`${actor.name} ${action}${target ? ` · ${target}` : ''}`}
      timestamp={timestamp}
      last={last}
    />
  )
}

// ─── AuditItem — With severity ────────────────────────────────────────────────
const SEVERITY_STYLES = {
  info:     { icon: 'ℹ', color: '#1E40AF', bg: '#DBEAFE' },
  warning:  { icon: '⚠', color: '#92400E', bg: '#FEF3C7' },
  error:    { icon: '✗', color: '#991B1B', bg: '#FEE2E2' },
  critical: { icon: '!', color: '#7F1D1D', bg: '#FCA5A5' },
}
interface AuditItemProps {
  severity:  keyof typeof SEVERITY_STYLES
  event:     string
  actor?:    string
  resource?: string
  timestamp: string
  last?:     boolean
}
export function AuditItem({ severity, event, actor, resource, timestamp, last }: AuditItemProps) {
  const s = SEVERITY_STYLES[severity]
  return (
    <TimelineItem
      icon={<span style={{ fontWeight: 700 }}>{s.icon}</span>}
      iconColor={s.color}
      title={event}
      subtitle={[actor, resource].filter(Boolean).join(' · ')}
      timestamp={timestamp}
      last={last}
    />
  )
}

// ─── NotificationItem — With action ──────────────────────────────────────────
interface NotificationItemProps {
  title:     string
  message:   string
  category?: string
  timestamp: string
  read?:     boolean
  onRead?:   () => void
  last?:     boolean
}
export function NotificationItem({ title, message, category, timestamp, read = false, onRead, last }: NotificationItemProps) {
  return (
    <TimelineItem
      icon={<span style={{ fontSize: '0.6875rem', fontWeight: 700 }}>{category?.[0]?.toUpperCase() ?? 'N'}</span>}
      iconColor={read ? '#9CA3AF' : '#0F1D3A'}
      title={title}
      subtitle={message}
      timestamp={timestamp}
      last={last}
    >
      {!read && onRead && (
        <button onClick={onRead} style={{ fontSize: '0.75rem', color: '#1E40AF', background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}>
          Mark as read
        </button>
      )}
    </TimelineItem>
  )
}

// ─── Timeline — Container ─────────────────────────────────────────────────────
interface TimelineProps { children: React.ReactNode; className?: string }
export function Timeline({ children, className = '' }: TimelineProps) {
  return <div className={className} style={{ padding: '0.25rem 0' }}>{children}</div>
}
