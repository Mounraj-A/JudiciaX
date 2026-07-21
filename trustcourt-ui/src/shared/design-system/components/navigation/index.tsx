import React from 'react'
import { motion } from 'framer-motion'
import { cn } from '@/shared/design-system/utils/cn'

// ─── Sidebar ──────────────────────────────────────────────────────────────────
interface SidebarItem {
  id: string
  label: string
  icon?: React.ReactNode
  href?: string
  badge?: string | number
  children?: SidebarItem[]
}

interface SidebarProps {
  items: SidebarItem[]
  collapsed?: boolean
  onCollapse?: (collapsed: boolean) => void
  activeId?: string
  onItemClick?: (item: SidebarItem) => void
  logo?: React.ReactNode
  footer?: React.ReactNode
}

function Sidebar({ items, collapsed = false, onCollapse, activeId, onItemClick, logo, footer }: SidebarProps) {
  return (
    <motion.aside
      animate={{ width: collapsed ? '4.5rem' : '16rem' }}
      transition={{ duration: 0.25, ease: [0.4, 0, 0.2, 1] }}
      className="flex flex-col h-full bg-[#0F1D3A] text-white overflow-hidden border-r border-[#1E3A8A]/30"
      aria-label="Primary navigation"
    >
      {/* Logo */}
      <div className="flex items-center h-16 px-4 border-b border-white/10 shrink-0">
        {logo ?? (
          <div className="flex items-center gap-3">
            <div className="h-8 w-8 rounded-lg bg-[#C2410C] flex items-center justify-center text-white font-bold text-sm shrink-0">
              TC
            </div>
            {!collapsed && (
              <motion.span
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="text-sm font-semibold text-white whitespace-nowrap"
              >
                TrustCourt
              </motion.span>
            )}
          </div>
        )}
        <button
          onClick={() => onCollapse?.(!collapsed)}
          className="ml-auto h-8 w-8 flex items-center justify-center rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-colors shrink-0"
          aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {collapsed ? '→' : '←'}
        </button>
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto py-4 space-y-1 px-2" role="navigation">
        {items.map((item) => (
          <button
            key={item.id}
            onClick={() => onItemClick?.(item)}
            className={cn(
              'w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all duration-150',
              activeId === item.id
                ? 'bg-white/15 text-white font-medium'
                : 'text-white/70 hover:bg-white/10 hover:text-white',
              collapsed ? 'justify-center' : ''
            )}
            aria-current={activeId === item.id ? 'page' : undefined}
          >
            {item.icon && (
              <span className="shrink-0 h-5 w-5 flex items-center justify-center" aria-hidden="true">
                {item.icon}
              </span>
            )}
            {!collapsed && (
              <motion.span
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="flex-1 text-left whitespace-nowrap"
              >
                {item.label}
              </motion.span>
            )}
            {!collapsed && item.badge && (
              <span className="ml-auto text-xs bg-[#C2410C] text-white rounded-full px-2 py-0.5">
                {item.badge}
              </span>
            )}
          </button>
        ))}
      </nav>

      {/* Footer */}
      {footer && !collapsed && (
        <div className="p-4 border-t border-white/10 shrink-0">{footer}</div>
      )}
    </motion.aside>
  )
}

// ─── TopNav ───────────────────────────────────────────────────────────────────
interface TopNavProps {
  title?: string
  actions?: React.ReactNode
  breadcrumb?: React.ReactNode
  className?: string
}

function TopNav({ title, actions, breadcrumb, className }: TopNavProps) {
  return (
    <header
      className={cn(
        'h-16 flex items-center justify-between px-6 bg-white border-b border-[#E5E7EB] shrink-0',
        className
      )}
      role="banner"
    >
      <div className="flex flex-col justify-center">
        {breadcrumb && <div className="mb-0.5">{breadcrumb}</div>}
        {title && <h1 className="text-base font-semibold text-[#111827]">{title}</h1>}
      </div>
      {actions && (
        <div className="flex items-center gap-3" role="toolbar">
          {actions}
        </div>
      )}
    </header>
  )
}

// ─── Breadcrumb ───────────────────────────────────────────────────────────────
interface BreadcrumbItem {
  label: string
  href?: string
}

interface BreadcrumbProps {
  items: BreadcrumbItem[]
}

function Breadcrumb({ items }: BreadcrumbProps) {
  return (
    <nav aria-label="Breadcrumb">
      <ol className="flex items-center gap-1 text-xs text-[#6B7280]">
        {items.map((item, i) => (
          <React.Fragment key={i}>
            {i > 0 && <span className="text-[#D1D5DB]">/</span>}
            {i === items.length - 1 ? (
              <span className="text-[#374151] font-medium" aria-current="page">{item.label}</span>
            ) : (
              <a href={item.href ?? '#'} className="hover:text-[#0F1D3A] transition-colors">
                {item.label}
              </a>
            )}
          </React.Fragment>
        ))}
      </ol>
    </nav>
  )
}

// ─── Tabs ─────────────────────────────────────────────────────────────────────
interface Tab {
  id: string
  label: string
  badge?: string | number
  disabled?: boolean
}

interface TabsProps {
  tabs: Tab[]
  activeTab: string
  onChange: (id: string) => void
  variant?: 'line' | 'pill'
}

function Tabs({ tabs, activeTab, onChange, variant = 'line' }: TabsProps) {
  return (
    <div
      role="tablist"
      aria-label="Tabs"
      className={cn(
        'flex',
        variant === 'line' ? 'border-b border-[#E5E7EB] gap-0' : 'gap-1 bg-[#F3F4F6] p-1 rounded-lg'
      )}
    >
      {tabs.map((tab) => (
        <button
          key={tab.id}
          role="tab"
          aria-selected={activeTab === tab.id}
          aria-controls={`tabpanel-${tab.id}`}
          id={`tab-${tab.id}`}
          disabled={tab.disabled}
          onClick={() => onChange(tab.id)}
          className={cn(
            'flex items-center gap-2 text-sm font-medium transition-all duration-150',
            'disabled:opacity-50 disabled:cursor-not-allowed',
            variant === 'line'
              ? cn(
                  'px-4 py-3 border-b-2 -mb-px',
                  activeTab === tab.id
                    ? 'border-[#0F1D3A] text-[#0F1D3A]'
                    : 'border-transparent text-[#6B7280] hover:text-[#374151] hover:border-[#D1D5DB]'
                )
              : cn(
                  'px-3 py-1.5 rounded-md',
                  activeTab === tab.id
                    ? 'bg-white text-[#111827] shadow-soft'
                    : 'text-[#6B7280] hover:text-[#374151]'
                )
          )}
        >
          {tab.label}
          {tab.badge != null && (
            <span className="text-xs bg-[#0F1D3A] text-white rounded-full px-1.5 py-0.5 leading-none">
              {tab.badge}
            </span>
          )}
        </button>
      ))}
    </div>
  )
}

// ─── Pagination ───────────────────────────────────────────────────────────────
interface PaginationProps {
  page: number
  total: number
  pageSize: number
  onChange: (page: number) => void
}

function Pagination({ page, total, pageSize, onChange }: PaginationProps) {
  const totalPages = Math.ceil(total / pageSize)
  const pages = Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
    const start = Math.max(1, Math.min(page - 2, totalPages - 4))
    return start + i
  })

  return (
    <nav aria-label="Pagination" className="flex items-center gap-1">
      <button
        onClick={() => onChange(page - 1)}
        disabled={page <= 1}
        className="h-8 w-8 flex items-center justify-center rounded-lg text-sm text-[#374151] hover:bg-[#F3F4F6] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        aria-label="Previous page"
      >
        ‹
      </button>
      {pages.map((p) => (
        <button
          key={p}
          onClick={() => onChange(p)}
          aria-current={p === page ? 'page' : undefined}
          className={cn(
            'h-8 w-8 flex items-center justify-center rounded-lg text-sm transition-colors',
            p === page
              ? 'bg-[#0F1D3A] text-white font-medium'
              : 'text-[#374151] hover:bg-[#F3F4F6]'
          )}
        >
          {p}
        </button>
      ))}
      <button
        onClick={() => onChange(page + 1)}
        disabled={page >= totalPages}
        className="h-8 w-8 flex items-center justify-center rounded-lg text-sm text-[#374151] hover:bg-[#F3F4F6] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        aria-label="Next page"
      >
        ›
      </button>
    </nav>
  )
}

export { Sidebar, TopNav, Breadcrumb, Tabs, Pagination }
export type { SidebarItem, SidebarProps }
