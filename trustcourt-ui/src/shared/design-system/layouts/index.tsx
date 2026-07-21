import React from 'react'
import { cn } from '@/shared/design-system/utils/cn'

// ─── Page Container ───────────────────────────────────────────────────────────
interface PageContainerProps {
  children: React.ReactNode
  className?: string
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'full'
}

const maxWidthMap = {
  sm:   'max-w-2xl',
  md:   'max-w-4xl',
  lg:   'max-w-5xl',
  xl:   'max-w-6xl',
  '2xl':'max-w-7xl',
  full: 'max-w-full',
}

function PageContainer({ children, className, maxWidth = '2xl' }: PageContainerProps) {
  return (
    <main className={cn('flex-1 overflow-auto bg-[#F8F9FA]', className)}>
      <div className={cn('mx-auto px-6 py-6', maxWidthMap[maxWidth])}>
        {children}
      </div>
    </main>
  )
}

// ─── Workspace Layout ─────────────────────────────────────────────────────────
interface WorkspaceLayoutProps {
  sidebar: React.ReactNode
  topNav: React.ReactNode
  children: React.ReactNode
  sidebarCollapsed?: boolean
}

function WorkspaceLayout({ sidebar, topNav, children, sidebarCollapsed }: WorkspaceLayoutProps) {
  return (
    <div className="flex h-screen overflow-hidden bg-[#F8F9FA]">
      {/* Sidebar */}
      <div
        className="shrink-0 flex flex-col"
        style={{ width: sidebarCollapsed ? '4.5rem' : '16rem', transition: 'width 0.25s ease' }}
      >
        {sidebar}
      </div>

      {/* Main area */}
      <div className="flex flex-col flex-1 overflow-hidden">
        {topNav}
        <div className="flex-1 overflow-y-auto relative">
          {children}
        </div>
      </div>
    </div>
  )
}

// ─── Split Layout ─────────────────────────────────────────────────────────────
interface SplitLayoutProps {
  left: React.ReactNode
  right: React.ReactNode
  ratio?: '1/3' | '1/2' | '2/3'
  gap?: number
}

function SplitLayout({ left, right, ratio = '1/2', gap = 6 }: SplitLayoutProps) {
  const gridClass = {
    '1/3': 'grid-cols-[1fr_2fr]',
    '1/2': 'grid-cols-2',
    '2/3': 'grid-cols-[2fr_1fr]',
  }[ratio]

  return (
    <div className={cn('grid', gridClass, `gap-${gap}`)}>
      <div>{left}</div>
      <div>{right}</div>
    </div>
  )
}

// ─── Dashboard Layout ─────────────────────────────────────────────────────────
interface DashboardLayoutProps {
  header?: React.ReactNode
  kpis?: React.ReactNode
  main?: React.ReactNode
  aside?: React.ReactNode
  children?: React.ReactNode
}

function DashboardLayout({ header, kpis, main, aside, children }: DashboardLayoutProps) {
  return (
    <div className="space-y-6">
      {header}
      {kpis && (
        <section aria-label="Key metrics" className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          {kpis}
        </section>
      )}
      {(main ?? aside) ? (
        <div className="grid grid-cols-1 lg:grid-cols-[1fr_20rem] gap-6">
          <div className="space-y-6">{main}</div>
          {aside && <aside className="space-y-6">{aside}</aside>}
        </div>
      ) : null}
      {children}
    </div>
  )
}

// ─── Section ──────────────────────────────────────────────────────────────────
interface SectionProps {
  title?: string
  description?: string
  actions?: React.ReactNode
  children: React.ReactNode
  className?: string
}

function Section({ title, description, actions, children, className }: SectionProps) {
  return (
    <section className={cn('space-y-4', className)}>
      {(title ?? actions) && (
        <div className="flex items-start justify-between">
          <div>
            {title && <h2 className="text-base font-semibold text-[#111827]">{title}</h2>}
            {description && <p className="text-sm text-[#6B7280] mt-0.5">{description}</p>}
          </div>
          {actions && <div className="flex items-center gap-2 ml-4">{actions}</div>}
        </div>
      )}
      {children}
    </section>
  )
}

// ─── Grid ─────────────────────────────────────────────────────────────────────
interface GridProps {
  children: React.ReactNode
  cols?: 1 | 2 | 3 | 4
  gap?: 4 | 6 | 8
  className?: string
}

function Grid({ children, cols = 3, gap = 6, className }: GridProps) {
  const colsClass = {
    1: 'grid-cols-1',
    2: 'grid-cols-1 sm:grid-cols-2',
    3: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3',
    4: 'grid-cols-2 lg:grid-cols-4',
  }[cols]

  return (
    <div className={cn('grid', colsClass, `gap-${gap}`, className)}>
      {children}
    </div>
  )
}

// ─── Empty Layout (for standalone pages) ──────────────────────────────────────
function EmptyLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-[#F8F9FA]">
      {children}
    </div>
  )
}

export { PageContainer, WorkspaceLayout, SplitLayout, DashboardLayout, Section, Grid, EmptyLayout }
