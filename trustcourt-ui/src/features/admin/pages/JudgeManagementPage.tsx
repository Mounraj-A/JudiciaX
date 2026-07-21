// ─── JudgeManagementPage — Full Backend Integration ───────────────────────────
// Connects to: JudgeAdministrationController (/admin/judges)
// Supports: Pagination, Search, Filters, Sort, Detail View, Assign, Transfer
import React, { useState, useMemo, useCallback, useEffect, useRef } from 'react'
import { useSelector } from 'react-redux'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'

import { Modal } from '@/shared/components/overlay/Modal'
import { AdminTable } from '../components/AdminTable'
import { AdminFormDialog } from '../components/AdminFormDialog'
import {
  useJudgeWorkloads,
  useJudgeWorkload,
  useAssignJudge,
  useTransferJudge,
} from '../hooks/useAdminJudgesClerks'
import { toast } from 'react-hot-toast'
import { selectUserRole } from '@/store/slices/authSlice'
import type { JudgeWorkloadResponse } from '../api/adminJudgeApi'
import {
  computeWorkloadCategory,
  computeWorkloadScore,
  type WorkloadCategory,
} from '../api/adminJudgeApi'

// ── Constants ─────────────────────────────────────────────────────────────────
const ADMIN_ROLES = ['ADMIN', 'ROLE_ADMIN', 'SUPER_ADMIN', 'ROLE_SUPER_ADMIN']

const WORKLOAD_CONFIG: Record<WorkloadCategory, { label: string; color: string; bg: string; bar: string }> = {
  LOW:        { label: 'Low',        color: '#065F46', bg: '#D1FAE5', bar: '#10B981' },
  MEDIUM:     { label: 'Medium',     color: '#92400E', bg: '#FEF3C7', bar: '#F59E0B' },
  HIGH:       { label: 'High',       color: '#C2410C', bg: '#FFEDD5', bar: '#F97316' },
  OVERLOADED: { label: 'Overloaded', color: '#991B1B', bg: '#FEE2E2', bar: '#EF4444' },
}

const SORT_FIELDS = [
  { value: 'judgeName',          label: 'Judge Name (A–Z)' },
  { value: 'judgeName_desc',     label: 'Judge Name (Z–A)' },
  { value: 'courtName',          label: 'Court (A–Z)' },
  { value: 'totalAssignedCases', label: 'Total Cases (High–Low)' },
  { value: 'activeCases',        label: 'Active Cases (High–Low)' },
  { value: 'pendingHearings',    label: 'Pending Hearings (High–Low)' },
  { value: 'disposedCases',      label: 'Disposed (High–Low)' },
] as const

// ── Shared styles ─────────────────────────────────────────────────────────────
const inp: React.CSSProperties = {
  width: '100%', padding: '0.5rem 0.75rem', borderRadius: '0.375rem',
  border: '1px solid #D1D5DB', fontSize: '0.875rem', background: '#fff',
  outline: 'none', boxSizing: 'border-box',
}
const lbl: React.CSSProperties = {
  display: 'block', fontSize: '0.8125rem', fontWeight: 600,
  color: '#374151', marginBottom: '0.3rem',
}

// ── Workload Badge ─────────────────────────────────────────────────────────────
function WorkloadBadge({ total }: { total: number }) {
  const cat = computeWorkloadCategory(total)
  const cfg = WORKLOAD_CONFIG[cat]
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: '0.3rem',
      padding: '0.2rem 0.55rem', borderRadius: '9999px',
      fontSize: '0.6875rem', fontWeight: 600,
      color: cfg.color, background: cfg.bg, whiteSpace: 'nowrap',
    }}>
      {cfg.label}
    </span>
  )
}

// ── Workload Bar ───────────────────────────────────────────────────────────────
function WorkloadBar({ total }: { total: number }) {
  const cat = computeWorkloadCategory(total)
  const score = computeWorkloadScore(total)
  const cfg = WORKLOAD_CONFIG[cat]
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
      <div style={{ width: 60, height: 6, background: '#E5E7EB', borderRadius: 3, overflow: 'hidden', flexShrink: 0 }}>
        <div style={{ height: '100%', width: `${score}%`, background: cfg.bar, borderRadius: 3, transition: 'width 0.3s' }} />
      </div>
      <WorkloadBadge total={total} />
    </div>
  )
}

// ── Judge Detail Modal ─────────────────────────────────────────────────────────
function JudgeDetailModal({ judge, onClose }: { judge: JudgeWorkloadResponse; onClose: () => void }) {
  const { data: detail, isLoading } = useJudgeWorkload(judge.judgeUuid)
  const d = detail ?? judge

  const stats = [
    ['Total Assigned', d.totalAssignedCases],
    ['Active Cases',   d.activeCases],
    ['Pending Hearings', d.pendingHearings],
    ['Disposed Cases', d.disposedCases],
    ['Reserved Judgments', d.reservedJudgments],
  ] as const

  return (
    <Modal isOpen onClose={onClose} title="Judge Profile">
      <div style={{ width: 600, maxWidth: '95vw' }}>
        {isLoading ? (
          <div style={{ padding: '2rem', textAlign: 'center', color: '#6B7280' }}>Loading…</div>
        ) : (
          <>
            {/* Header */}
            <div style={{
              display: 'flex', alignItems: 'center', gap: '1rem',
              padding: '0 0 1.25rem', marginBottom: '1.25rem',
              borderBottom: '1px solid #E5E7EB',
            }}>
              <div style={{
                width: 52, height: 52, borderRadius: '50%',
                background: '#1E3A8A', display: 'flex', alignItems: 'center',
                justifyContent: 'center', color: '#fff', fontSize: '1.25rem', fontWeight: 700, flexShrink: 0,
              }}>
                {d.judgeName?.charAt(0)?.toUpperCase() ?? 'J'}
              </div>
              <div>
                <h3 style={{ margin: 0, fontSize: '1.0625rem', fontWeight: 700, color: '#111827' }}>{d.judgeName}</h3>
                <div style={{ fontSize: '0.875rem', color: '#6B7280', marginTop: '0.15rem' }}>{d.designation || '—'}</div>
                <div style={{ marginTop: '0.4rem' }}><WorkloadBadge total={d.totalAssignedCases} /></div>
              </div>
            </div>

            {/* Info grid */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.875rem', marginBottom: '1.25rem' }}>
              {[
                ['Judge ID',       d.judgeIdNumber || '—'],
                ['Judge UUID',     <span style={{ fontFamily: 'monospace', fontSize: '0.75rem', color: '#374151' }}>{d.judgeUuid?.substring(0, 16)}…</span>],
                ['Court',          d.courtName || '—'],
                ['Specialization', d.specialization || '—'],
              ].map(([k, v]) => (
                <div key={String(k)}>
                  <div style={{ fontSize: '0.6875rem', fontWeight: 700, color: '#9CA3AF', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '0.2rem' }}>{k}</div>
                  <div style={{ fontSize: '0.9375rem', color: '#111827' }}>{v}</div>
                </div>
              ))}
            </div>

            {/* Case stats */}
            <div style={{
              display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '0.5rem',
              background: '#F9FAFB', border: '1px solid #E5E7EB',
              borderRadius: '0.5rem', padding: '0.875rem',
            }}>
              {stats.map(([label, val]) => (
                <div key={label} style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '1.375rem', fontWeight: 700, color: '#111827' }}>{val}</div>
                  <div style={{ fontSize: '0.6875rem', color: '#6B7280', marginTop: '0.15rem' }}>{label}</div>
                </div>
              ))}
            </div>

            {/* Workload bar */}
            <div style={{ marginTop: '1rem' }}>
              <div style={{ fontSize: '0.8125rem', fontWeight: 600, color: '#374151', marginBottom: '0.375rem' }}>
                Workload ({computeWorkloadScore(d.totalAssignedCases)}%)
              </div>
              <div style={{ height: 10, background: '#E5E7EB', borderRadius: 5, overflow: 'hidden' }}>
                <div style={{
                  height: '100%',
                  width: `${computeWorkloadScore(d.totalAssignedCases)}%`,
                  background: WORKLOAD_CONFIG[computeWorkloadCategory(d.totalAssignedCases)].bar,
                  borderRadius: 5, transition: 'width 0.4s',
                }} />
              </div>
            </div>
          </>
        )}
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.25rem', paddingTop: '0.875rem', borderTop: '1px solid #E5E7EB' }}>
          <Button variant="ghost" onClick={onClose}>Close</Button>
        </div>
      </div>
    </Modal>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Main Page
// ─────────────────────────────────────────────────────────────────────────────

export function JudgeManagementPage() {
  // ── Pagination ────────────────────────────────────────────────────────────
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)

  // ── Data ──────────────────────────────────────────────────────────────────
  const { data: pageData, isLoading, error, refetch } = useJudgeWorkloads(page, size)
  const judges: JudgeWorkloadResponse[] = pageData?.content ?? []

  // ── RBAC ──────────────────────────────────────────────────────────────────
  const userRole = useSelector(selectUserRole)
  const isAdmin  = ADMIN_ROLES.includes((userRole ?? '') as string)

  // ── Search ────────────────────────────────────────────────────────────────
  const [searchInput, setSearchInput] = useState('')
  const [search, setSearch]           = useState('')
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const handleSearchChange = (val: string) => {
    setSearchInput(val)
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => { setSearch(val); setPage(0) }, 400)
  }
  useEffect(() => () => { if (debounceRef.current) clearTimeout(debounceRef.current) }, [])

  // ── Filters ───────────────────────────────────────────────────────────────
  const [filterCourt,    setFilterCourt]    = useState('')
  const [filterDesig,    setFilterDesig]    = useState('')
  const [filterSpec,     setFilterSpec]     = useState('')
  const [filterWorkload, setFilterWorkload] = useState('')
  const [activeFilters,  setActiveFilters]  = useState({ court: '', desig: '', spec: '', workload: '' })

  const applyFilters = () => {
    setActiveFilters({ court: filterCourt, desig: filterDesig, spec: filterSpec, workload: filterWorkload })
    setPage(0)
  }
  const clearFilters = () => {
    setFilterCourt(''); setFilterDesig(''); setFilterSpec(''); setFilterWorkload('')
    setActiveFilters({ court: '', desig: '', spec: '', workload: '' })
    setSearch(''); setSearchInput(''); setPage(0)
  }
  const hasActiveFilters = !!(search || activeFilters.court || activeFilters.desig || activeFilters.spec || activeFilters.workload)

  // ── Sort ──────────────────────────────────────────────────────────────────
  const [sortField, setSortField] = useState<string>('judgeName')

  // ── Computed display list ─────────────────────────────────────────────────
  const displayedJudges = useMemo(() => {
    let rows = [...judges]

    // Search
    const q = search.trim().toLowerCase()
    if (q) {
      rows = rows.filter(j =>
        j.judgeName?.toLowerCase().includes(q) ||
        j.judgeIdNumber?.toLowerCase().includes(q) ||
        j.courtName?.toLowerCase().includes(q) ||
        j.designation?.toLowerCase().includes(q) ||
        j.specialization?.toLowerCase().includes(q)
      )
    }

    // Filters
    if (activeFilters.court)    rows = rows.filter(j => j.courtName?.toLowerCase().includes(activeFilters.court.toLowerCase()))
    if (activeFilters.desig)    rows = rows.filter(j => j.designation?.toLowerCase().includes(activeFilters.desig.toLowerCase()))
    if (activeFilters.spec)     rows = rows.filter(j => j.specialization?.toLowerCase().includes(activeFilters.spec.toLowerCase()))
    if (activeFilters.workload) rows = rows.filter(j => computeWorkloadCategory(j.totalAssignedCases) === activeFilters.workload)

    // Sort
    rows.sort((a, b) => {
      switch (sortField) {
        case 'judgeName':           return (a.judgeName ?? '').localeCompare(b.judgeName ?? '')
        case 'judgeName_desc':      return (b.judgeName ?? '').localeCompare(a.judgeName ?? '')
        case 'courtName':           return (a.courtName ?? '').localeCompare(b.courtName ?? '')
        case 'totalAssignedCases':  return b.totalAssignedCases - a.totalAssignedCases
        case 'activeCases':         return b.activeCases - a.activeCases
        case 'pendingHearings':     return b.pendingHearings - a.pendingHearings
        case 'disposedCases':       return b.disposedCases - a.disposedCases
        default:                    return 0
      }
    })
    return rows
  }, [judges, search, activeFilters, sortField])

  // ── Mutations ─────────────────────────────────────────────────────────────
  const assignMutation   = useAssignJudge()
  const transferMutation = useTransferJudge()

  // ── Modal State ───────────────────────────────────────────────────────────
  const [detailJudge,    setDetailJudge]    = useState<JudgeWorkloadResponse | null>(null)
  const [assignJudge,    setAssignJudge]    = useState<JudgeWorkloadResponse | null>(null)
  const [transferJudge,  setTransferJudge]  = useState<JudgeWorkloadResponse | null>(null)

  const [assignForm,   setAssignForm]   = useState({ caseUuid: '', reason: '' })
  const [transferForm, setTransferForm] = useState({ caseUuid: '', reason: '' })
  const [formError,    setFormError]    = useState<string | null>(null)

  const openAssign   = useCallback((j: JudgeWorkloadResponse) => { setAssignJudge(j); setAssignForm({ caseUuid: '', reason: '' }); setFormError(null) }, [])
  const openTransfer = useCallback((j: JudgeWorkloadResponse) => { setTransferJudge(j); setTransferForm({ caseUuid: '', reason: '' }); setFormError(null) }, [])

  // ── Assign handler ────────────────────────────────────────────────────────
  const handleAssign = async () => {
    setFormError(null)
    if (!assignForm.caseUuid.trim()) { setFormError('Case UUID is required.'); return }
    if (!assignJudge) return
    try {
      await assignMutation.mutateAsync({
        caseUuid:      assignForm.caseUuid.trim(),
        judgeUserUuid: assignJudge.judgeUuid,
        reason:        assignForm.reason || undefined,
      })
      toast.success(`Judge "${assignJudge.judgeName}" assigned to case`)
      setAssignJudge(null)
    } catch (err: any) {
      const msg = err.message || 'Assignment failed'
      setFormError(msg)
      toast.error(msg)
    }
  }

  // ── Transfer handler ──────────────────────────────────────────────────────
  const handleTransfer = async () => {
    setFormError(null)
    if (!transferForm.caseUuid.trim()) { setFormError('Case UUID is required.'); return }
    if (!transferJudge) return
    try {
      await transferMutation.mutateAsync({
        judgeUserUuid: transferJudge.judgeUuid,
        caseUuid:      transferForm.caseUuid.trim(),
        reason:        transferForm.reason || 'Administrative reassignment',
      })
      toast.success(`Judge "${transferJudge.judgeName}" transferred on case`)
      setTransferJudge(null)
    } catch (err: any) {
      const msg = err.message || 'Transfer failed'
      setFormError(msg)
      toast.error(msg)
    }
  }

  // ── Unique filter options from loaded data ────────────────────────────────
  const uniqueCourts = useMemo(() => [...new Set(judges.map(j => j.courtName).filter(Boolean))].sort(), [judges])
  const uniqueDesigs = useMemo(() => [...new Set(judges.map(j => j.designation).filter(Boolean))].sort(), [judges])
  const uniqueSpecs  = useMemo(() => [...new Set(judges.map(j => j.specialization).filter(Boolean))].sort(), [judges])

  // ── Table columns ─────────────────────────────────────────────────────────
  const columns = useMemo(() => [
    {
      id: 'judgeIdNumber', header: 'Judge ID',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ fontFamily: 'monospace', fontSize: '0.8125rem', color: '#374151', fontWeight: 600 }}>
          {row.judgeIdNumber || '—'}
        </span>
      ),
    },
    {
      id: 'judgeName', header: 'Judge Name',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <button
          onClick={() => setDetailJudge(row)}
          style={{
            background: 'none', border: 'none', cursor: 'pointer',
            color: '#1E40AF', fontWeight: 600, fontSize: '0.875rem',
            textAlign: 'left', padding: 0, textDecoration: 'underline dotted',
          }}
        >
          {row.judgeName || '—'}
        </button>
      ),
    },
    {
      id: 'designation', header: 'Designation',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ fontSize: '0.875rem', color: '#374151' }}>{row.designation || '—'}</span>
      ),
    },
    {
      id: 'courtName', header: 'Court',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ fontSize: '0.875rem', color: '#374151' }}>{row.courtName || '—'}</span>
      ),
    },
    {
      id: 'specialization', header: 'Specialization',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ fontSize: '0.8125rem', color: '#6B7280' }}>{row.specialization || '—'}</span>
      ),
    },
    {
      id: 'totalAssignedCases', header: 'Total',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ fontWeight: 600, color: '#111827', fontSize: '0.875rem' }}>{row.totalAssignedCases}</span>
      ),
    },
    {
      id: 'activeCases', header: 'Active',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ color: '#1D4ED8', fontWeight: 500, fontSize: '0.875rem' }}>{row.activeCases}</span>
      ),
    },
    {
      id: 'pendingHearings', header: 'Pending',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ color: '#92400E', fontWeight: 500, fontSize: '0.875rem' }}>{row.pendingHearings}</span>
      ),
    },
    {
      id: 'disposedCases', header: 'Disposed',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ color: '#065F46', fontWeight: 500, fontSize: '0.875rem' }}>{row.disposedCases}</span>
      ),
    },
    {
      id: 'reservedJudgments', header: 'Reserved',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <span style={{ color: '#6B7280', fontSize: '0.875rem' }}>{row.reservedJudgments}</span>
      ),
    },
    {
      id: 'workload', header: 'Workload',
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <WorkloadBar total={row.totalAssignedCases} />
      ),
    },
    ...(isAdmin ? [{
      id: 'actions', header: 'Actions', align: 'right' as const,
      renderCell: (_v: unknown, row: JudgeWorkloadResponse) => (
        <div style={{ display: 'flex', gap: '0.375rem', justifyContent: 'flex-end' }}>
          <Button variant="ghost" size="sm" onClick={() => setDetailJudge(row)}>View</Button>
          <Button variant="ghost" size="sm" onClick={() => openAssign(row)}>Assign</Button>
          <Button variant="ghost" size="sm" onClick={() => openTransfer(row)}>Transfer</Button>
        </div>
      ),
    }] : []),
  ], [isAdmin, openAssign, openTransfer])

  // ─────────────────────────────────────────────────────────────────────────
  return (
    <ContentContainer>
      <PageHeader
        title="Judge Administration"
        subtitle="Monitor workloads and manage case assignments across all judges."
      />

      <Section>
        {/* ── Search & Filter Bar ──────────────────────────────────────────── */}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem', marginBottom: '1rem', alignItems: 'flex-end' }}>
          {/* Search */}
          <div style={{ flex: '1 1 220px', minWidth: 180 }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Search</label>
            <input
              style={inp}
              value={searchInput}
              onChange={e => handleSearchChange(e.target.value)}
              placeholder="Name, ID, court, designation…"
            />
          </div>

          {/* Court filter */}
          <div style={{ flex: '0 1 180px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Court</label>
            <select style={inp} value={filterCourt} onChange={e => setFilterCourt(e.target.value)}>
              <option value="">All Courts</option>
              {uniqueCourts.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>

          {/* Designation filter */}
          <div style={{ flex: '0 1 180px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Designation</label>
            <select style={inp} value={filterDesig} onChange={e => setFilterDesig(e.target.value)}>
              <option value="">All Designations</option>
              {uniqueDesigs.map(d => <option key={d} value={d}>{d}</option>)}
            </select>
          </div>

          {/* Specialization filter */}
          <div style={{ flex: '0 1 180px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Specialization</label>
            <select style={inp} value={filterSpec} onChange={e => setFilterSpec(e.target.value)}>
              <option value="">All Specializations</option>
              {uniqueSpecs.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>

          {/* Workload category filter */}
          <div style={{ flex: '0 1 160px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Workload</label>
            <select style={inp} value={filterWorkload} onChange={e => setFilterWorkload(e.target.value)}>
              <option value="">All</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="OVERLOADED">Overloaded</option>
            </select>
          </div>

          {/* Sort */}
          <div style={{ flex: '0 1 200px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Sort By</label>
            <select style={inp} value={sortField} onChange={e => setSortField(e.target.value)}>
              {SORT_FIELDS.map(f => <option key={f.value} value={f.value}>{f.label}</option>)}
            </select>
          </div>

          {/* Apply / Clear */}
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <Button variant="primary" size="sm" onClick={applyFilters}>Apply</Button>
            {hasActiveFilters && <Button variant="ghost" size="sm" onClick={clearFilters}>Clear</Button>}
          </div>
        </div>

        {/* Active filter chips */}
        {hasActiveFilters && (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.375rem', marginBottom: '0.75rem' }}>
            {search && <span style={{ padding: '0.2rem 0.5rem', borderRadius: '9999px', background: '#DBEAFE', color: '#1E40AF', fontSize: '0.75rem', fontWeight: 600 }}>Search: {search}</span>}
            {activeFilters.court && <span style={{ padding: '0.2rem 0.5rem', borderRadius: '9999px', background: '#FEF3C7', color: '#92400E', fontSize: '0.75rem', fontWeight: 600 }}>Court: {activeFilters.court}</span>}
            {activeFilters.desig && <span style={{ padding: '0.2rem 0.5rem', borderRadius: '9999px', background: '#EDE9FE', color: '#6D28D9', fontSize: '0.75rem', fontWeight: 600 }}>Designation: {activeFilters.desig}</span>}
            {activeFilters.workload && <span style={{ padding: '0.2rem 0.5rem', borderRadius: '9999px', background: '#FFE4E6', color: '#BE123C', fontSize: '0.75rem', fontWeight: 600 }}>Workload: {activeFilters.workload}</span>}
          </div>
        )}

        {/* ── Table ───────────────────────────────────────────────────────── */}
        <AdminTable
          data={displayedJudges as any[]}
          columns={columns}
          rowId="judgeUuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage={hasActiveFilters ? 'No judges match your search or filter criteria.' : 'No judges found.'}
          onRefresh={() => refetch()}
          pagination={pageData ? {
            page: pageData.number,
            size: pageData.size,
            totalElements: pageData.totalElements,
            totalPages: pageData.totalPages,
            onPageChange: setPage,
            onSizeChange: (s) => { setSize(s); setPage(0) },
          } : undefined}
        />
      </Section>

      {/* ── Judge Detail Modal ─────────────────────────────────────────────── */}
      {detailJudge && (
        <JudgeDetailModal
          judge={detailJudge}
          onClose={() => setDetailJudge(null)}
        />
      )}

      {/* ── Assign Judge to Case ───────────────────────────────────────────── */}
      <AdminFormDialog
        isOpen={!!assignJudge}
        onClose={() => setAssignJudge(null)}
        title={assignJudge ? `Assign "${assignJudge.judgeName}" to Case` : 'Assign Judge'}
        isLoading={assignMutation.isPending}
        onSubmit={handleAssign}
        submitLabel="Assign Judge"
        error={formError}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div style={{ padding: '0.75rem 1rem', background: '#F0FDF4', border: '1px solid #BBF7D0', borderRadius: '0.375rem', fontSize: '0.875rem', color: '#166534' }}>
            <strong>Judge:</strong> {assignJudge?.judgeName} — {assignJudge?.designation}<br />
            <strong>Court:</strong> {assignJudge?.courtName || '—'}
          </div>
          <div>
            <label style={lbl}>Case UUID *</label>
            <input
              style={inp}
              value={assignForm.caseUuid}
              onChange={e => setAssignForm(p => ({ ...p, caseUuid: e.target.value }))}
              placeholder="Enter the case UUID to assign this judge"
            />
            <span style={{ fontSize: '0.75rem', color: '#9CA3AF' }}>The judge will be assigned to the specified open case.</span>
          </div>
          <div>
            <label style={lbl}>Reason (optional)</label>
            <textarea
              style={inp}
              rows={2}
              value={assignForm.reason}
              onChange={e => setAssignForm(p => ({ ...p, reason: e.target.value }))}
              placeholder="e.g., Expertise match"
            />
          </div>
        </div>
      </AdminFormDialog>

      {/* ── Transfer Judge on Case ─────────────────────────────────────────── */}
      <AdminFormDialog
        isOpen={!!transferJudge}
        onClose={() => setTransferJudge(null)}
        title={transferJudge ? `Transfer "${transferJudge.judgeName}"` : 'Transfer Judge'}
        isLoading={transferMutation.isPending}
        onSubmit={handleTransfer}
        submitLabel="Transfer Judge"
        error={formError}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div style={{ padding: '0.75rem 1rem', background: '#FFF7ED', border: '1px solid #FED7AA', borderRadius: '0.375rem', fontSize: '0.875rem', color: '#9A3412' }}>
            <strong>Judge:</strong> {transferJudge?.judgeName} — {transferJudge?.designation}<br />
            <strong>Court:</strong> {transferJudge?.courtName || '—'}<br />
            <em style={{ fontSize: '0.8125rem' }}>This replaces the current judge assignment on the specified case.</em>
          </div>
          <div>
            <label style={lbl}>Case UUID *</label>
            <input
              style={inp}
              value={transferForm.caseUuid}
              onChange={e => setTransferForm(p => ({ ...p, caseUuid: e.target.value }))}
              placeholder="Enter the case UUID to reassign"
            />
            <span style={{ fontSize: '0.75rem', color: '#9CA3AF' }}>This judge will replace the current judge on the specified case.</span>
          </div>
          <div>
            <label style={lbl}>Reason</label>
            <textarea
              style={inp}
              rows={2}
              value={transferForm.reason}
              onChange={e => setTransferForm(p => ({ ...p, reason: e.target.value }))}
              placeholder="e.g., Workload rebalancing, Conflict of interest"
            />
          </div>
        </div>
      </AdminFormDialog>
    </ContentContainer>
  )
}
