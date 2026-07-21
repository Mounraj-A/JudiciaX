// ─── CourtManagementPage — Full Backend Integration ───────────────────────────
// Connects to: CourtManagementController (/admin/courts)
// Features: Search, Filter, Sort, Pagination, Bench CRUD, Room CRUD, Detail View
import React, { useState, useMemo, useCallback, useEffect, useRef } from 'react'
import { useSelector } from 'react-redux'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge } from '@/shared/components/badges'
import { Modal } from '@/shared/components/overlay/Modal'
import { AdminTable } from '../components/AdminTable'
import { AdminFormDialog, ConfirmDialog } from '../components/AdminFormDialog'
import {
  useAdminCourts,
  useCreateCourt,
  useUpdateCourt,
  useDeleteCourt,
  useCourtBenches,
  useCourtRooms,
  useCreateBench,
  useUpdateBench,
  useDeleteBench,
  useCreateRoom,
  useUpdateRoom,
  useDeleteRoom,
} from '../hooks/useAdminCourts'
import { toast } from 'react-hot-toast'
import { selectUserRole } from '@/store/slices/authSlice'
import type {
  CourtResponse,
  CourtRequest,
  BenchResponse,
  BenchRequest,
  CourtRoomResponse,
  CourtRoomRequest,
} from '../api/adminCourtApi'
import { COURT_TYPES, BENCH_TYPES } from '../api/adminCourtApi'

// ── Constants ─────────────────────────────────────────────────────────────────
const ADMIN_ROLES = ['ADMIN', 'ROLE_ADMIN', 'SUPER_ADMIN', 'ROLE_SUPER_ADMIN']

const SORT_OPTIONS = [
  { value: 'courtName',  dir: 'asc',  label: 'Name (A–Z)' },
  { value: 'courtName',  dir: 'desc', label: 'Name (Z–A)' },
  { value: 'courtType',  dir: 'asc',  label: 'Type (A–Z)' },
  { value: 'state',      dir: 'asc',  label: 'State (A–Z)' },
  { value: 'district',   dir: 'asc',  label: 'District (A–Z)' },
  { value: 'createdAt',  dir: 'desc', label: 'Newest First' },
  { value: 'createdAt',  dir: 'asc',  label: 'Oldest First' },
  { value: 'updatedAt',  dir: 'desc', label: 'Recently Updated' },
  { value: 'isActive',   dir: 'asc',  label: 'Status' },
] as const

const DEFAULT_COURT_FORM: CourtRequest = {
  courtCode: '',
  courtName: '',
  courtType: 'DISTRICT_COURT',
  state: '',
  district: '',
  address: '',
  phoneNumber: '',
  email: '',
  isActive: true,
}

// ── Input styles (shared inline) ──────────────────────────────────────────────
const inp: React.CSSProperties = {
  width: '100%', padding: '0.5rem 0.75rem', borderRadius: '0.375rem',
  border: '1px solid #D1D5DB', fontSize: '0.875rem', background: '#fff',
  outline: 'none', boxSizing: 'border-box',
}
const lbl: React.CSSProperties = {
  display: 'block', fontSize: '0.8125rem', fontWeight: 600,
  color: '#374151', marginBottom: '0.35rem',
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function fmt(dt: string | null | undefined) {
  if (!dt) return '—'
  try { return new Intl.DateTimeFormat('en-IN', { dateStyle: 'medium' }).format(new Date(dt)) }
  catch { return dt }
}

function courtTypeLabel(type: string) {
  return COURT_TYPES.find(t => t.value === type)?.label ?? type
}

// ─────────────────────────────────────────────────────────────────────────────
//  Sub-components: Bench & Room management panels (used in detail drawer)
// ─────────────────────────────────────────────────────────────────────────────

function BenchPanel({ court }: { court: CourtResponse }) {
  const { data: benches = [], isLoading } = useCourtBenches(court.uuid)
  const createMutation = useCreateBench()
  const updateMutation = useUpdateBench()
  const deleteMutation = useDeleteBench()

  const [isAddOpen, setIsAddOpen] = useState(false)
  const [editBench, setEditBench] = useState<BenchResponse | null>(null)
  const [deleteBench, setDeleteBench] = useState<BenchResponse | null>(null)
  const [form, setForm] = useState<BenchRequest>({
    courtUuid: court.uuid, benchNumber: '', benchType: 'SINGLE', description: '', isActive: true,
  })

  const openAdd = () => {
    setForm({ courtUuid: court.uuid, benchNumber: '', benchType: 'SINGLE', description: '', isActive: true })
    setIsAddOpen(true)
  }
  const openEdit = (b: BenchResponse) => {
    setEditBench(b)
    setForm({ courtUuid: court.uuid, benchNumber: b.benchNumber, benchType: b.benchType, description: b.description ?? '', isActive: b.isActive })
  }

  const handleSave = async () => {
    try {
      if (editBench) {
        await updateMutation.mutateAsync({ uuid: editBench.uuid, data: form })
        toast.success('Bench updated')
        setEditBench(null)
      } else {
        await createMutation.mutateAsync(form)
        toast.success('Bench created')
        setIsAddOpen(false)
      }
    } catch (err: any) { toast.error(err.message || 'Failed to save bench') }
  }

  const BenchForm = (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
      <div>
        <label style={lbl}>Bench Number *</label>
        <input style={inp} value={form.benchNumber} onChange={e => setForm(p => ({ ...p, benchNumber: e.target.value }))} placeholder="e.g. B-01" />
      </div>
      <div>
        <label style={lbl}>Bench Type</label>
        <select style={inp} value={form.benchType} onChange={e => setForm(p => ({ ...p, benchType: e.target.value }))}>
          {BENCH_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
        </select>
      </div>
      <div>
        <label style={lbl}>Description</label>
        <textarea style={inp} rows={2} value={form.description ?? ''} onChange={e => setForm(p => ({ ...p, description: e.target.value }))} />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <input type="checkbox" id="bench-active" checked={!!form.isActive} onChange={e => setForm(p => ({ ...p, isActive: e.target.checked }))} />
        <label htmlFor="bench-active" style={{ fontSize: '0.875rem', color: '#374151' }}>Active</label>
      </div>
    </div>
  )

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
        <h4 style={{ margin: 0, fontSize: '0.9375rem', fontWeight: 600, color: '#111827' }}>
          Benches ({isLoading ? '…' : benches.length})
        </h4>
        <Button size="sm" onClick={openAdd}>+ Add Bench</Button>
      </div>

      {isLoading ? (
        <p style={{ fontSize: '0.875rem', color: '#6B7280' }}>Loading…</p>
      ) : benches.length === 0 ? (
        <p style={{ fontSize: '0.875rem', color: '#6B7280', textAlign: 'center', padding: '1rem', background: '#F9FAFB', borderRadius: '0.375rem' }}>
          No benches found. Add the first bench.
        </p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          {benches.map((b: BenchResponse) => (
            <div key={b.uuid} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '0.625rem 0.875rem', background: '#F9FAFB', border: '1px solid #E5E7EB',
              borderRadius: '0.375rem',
            }}>
              <div>
                <span style={{ fontWeight: 600, fontSize: '0.875rem', color: '#111827' }}>{b.benchNumber}</span>
                <span style={{ fontSize: '0.8125rem', color: '#6B7280', marginLeft: '0.5rem' }}>
                  {BENCH_TYPES.find(t => t.value === b.benchType)?.label ?? b.benchType}
                </span>
                {b.description && <span style={{ fontSize: '0.75rem', color: '#9CA3AF', marginLeft: '0.5rem' }}>{b.description}</span>}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <StatusBadge status={b.isActive ? 'active' : 'inactive'} />
                <Button variant="ghost" size="sm" onClick={() => toast('Judge-to-Bench assignment is pending backend integration.', { icon: 'ℹ️' })}>Manage Judges</Button>
                <Button variant="ghost" size="sm" onClick={() => openEdit(b)}>Edit</Button>
                <Button variant="ghost" size="sm" style={{ color: '#DC2626' }} onClick={() => setDeleteBench(b)}>Delete</Button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add Bench Dialog */}
      <AdminFormDialog isOpen={isAddOpen} onClose={() => setIsAddOpen(false)} title="Add Bench"
        isLoading={createMutation.isPending} onSubmit={handleSave} submitLabel="Create Bench">
        {BenchForm}
      </AdminFormDialog>

      {/* Edit Bench Dialog */}
      <AdminFormDialog isOpen={!!editBench} onClose={() => setEditBench(null)} title="Edit Bench"
        isLoading={updateMutation.isPending} onSubmit={handleSave} submitLabel="Save Changes">
        {BenchForm}
      </AdminFormDialog>

      {/* Delete Bench Confirm */}
      <ConfirmDialog
        isOpen={!!deleteBench}
        onClose={() => setDeleteBench(null)}
        title="Delete Bench"
        description={`Delete bench "${deleteBench?.benchNumber}"? This cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteMutation.isPending}
        onConfirm={async () => {
          try {
            await deleteMutation.mutateAsync({ benchUuid: deleteBench!.uuid, courtUuid: court.uuid })
            toast.success('Bench deleted')
            setDeleteBench(null)
          } catch (err: any) { toast.error(err.message || 'Failed to delete bench') }
        }}
      />
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────

function RoomPanel({ court }: { court: CourtResponse }) {
  const { data: rooms = [], isLoading } = useCourtRooms(court.uuid)
  const createMutation = useCreateRoom()
  const updateMutation = useUpdateRoom()
  const deleteMutation = useDeleteRoom()

  const [isAddOpen, setIsAddOpen] = useState(false)
  const [editRoom, setEditRoom] = useState<CourtRoomResponse | null>(null)
  const [deleteRoom, setDeleteRoom] = useState<CourtRoomResponse | null>(null)
  const [form, setForm] = useState<CourtRoomRequest>({
    courtUuid: court.uuid, roomNumber: '', floor: '', capacity: undefined, hasVideoConferencing: false, isActive: true,
  })

  const openAdd = () => {
    setForm({ courtUuid: court.uuid, roomNumber: '', floor: '', capacity: undefined, hasVideoConferencing: false, isActive: true })
    setIsAddOpen(true)
  }
  const openEdit = (r: CourtRoomResponse) => {
    setEditRoom(r)
    setForm({ courtUuid: court.uuid, roomNumber: r.roomNumber, floor: r.floor ?? '', capacity: r.capacity ?? undefined, hasVideoConferencing: r.hasVideoConferencing, isActive: r.isActive })
  }

  const handleSave = async () => {
    try {
      if (editRoom) {
        await updateMutation.mutateAsync({ uuid: editRoom.uuid, data: form })
        toast.success('Room updated')
        setEditRoom(null)
      } else {
        await createMutation.mutateAsync(form)
        toast.success('Room created')
        setIsAddOpen(false)
      }
    } catch (err: any) { toast.error(err.message || 'Failed to save room') }
  }

  const RoomForm = (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
      <div>
        <label style={lbl}>Room Number *</label>
        <input style={inp} value={form.roomNumber} onChange={e => setForm(p => ({ ...p, roomNumber: e.target.value }))} placeholder="e.g. CR-01" />
      </div>
      <div>
        <label style={lbl}>Floor</label>
        <input style={inp} value={form.floor ?? ''} onChange={e => setForm(p => ({ ...p, floor: e.target.value }))} placeholder="e.g. Ground" />
      </div>
      <div>
        <label style={lbl}>Capacity</label>
        <input style={inp} type="number" min={0} value={form.capacity ?? ''} onChange={e => setForm(p => ({ ...p, capacity: e.target.value ? Number(e.target.value) : undefined }))} />
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'flex-end', gap: '0.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <input type="checkbox" id="room-vc" checked={!!form.hasVideoConferencing} onChange={e => setForm(p => ({ ...p, hasVideoConferencing: e.target.checked }))} />
          <label htmlFor="room-vc" style={{ fontSize: '0.875rem', color: '#374151' }}>Video Conferencing</label>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <input type="checkbox" id="room-active" checked={!!form.isActive} onChange={e => setForm(p => ({ ...p, isActive: e.target.checked }))} />
          <label htmlFor="room-active" style={{ fontSize: '0.875rem', color: '#374151' }}>Active</label>
        </div>
      </div>
    </div>
  )

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
        <h4 style={{ margin: 0, fontSize: '0.9375rem', fontWeight: 600, color: '#111827' }}>
          Court Rooms ({isLoading ? '…' : rooms.length})
        </h4>
        <Button size="sm" onClick={openAdd}>+ Add Room</Button>
      </div>

      {isLoading ? (
        <p style={{ fontSize: '0.875rem', color: '#6B7280' }}>Loading…</p>
      ) : rooms.length === 0 ? (
        <p style={{ fontSize: '0.875rem', color: '#6B7280', textAlign: 'center', padding: '1rem', background: '#F9FAFB', borderRadius: '0.375rem' }}>
          No rooms found. Add the first court room.
        </p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          {rooms.map((r: CourtRoomResponse) => (
            <div key={r.uuid} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '0.625rem 0.875rem', background: '#F9FAFB', border: '1px solid #E5E7EB',
              borderRadius: '0.375rem',
            }}>
              <div>
                <span style={{ fontWeight: 600, fontSize: '0.875rem', color: '#111827' }}>{r.roomNumber}</span>
                {r.floor && <span style={{ fontSize: '0.8125rem', color: '#6B7280', marginLeft: '0.5rem' }}>Floor: {r.floor}</span>}
                {r.capacity != null && <span style={{ fontSize: '0.8125rem', color: '#6B7280', marginLeft: '0.5rem' }}>Cap: {r.capacity}</span>}
                {r.hasVideoConferencing && <span style={{ fontSize: '0.75rem', color: '#2563EB', marginLeft: '0.5rem' }}>📹 VC</span>}
                <div style={{ fontSize: '0.75rem', color: '#9CA3AF', marginTop: '0.25rem', fontStyle: 'italic' }}>Bench assignment not available.</div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <StatusBadge status={r.isActive ? 'active' : 'inactive'} />
                <Button variant="ghost" size="sm" onClick={() => openEdit(r)}>Edit</Button>
                <Button variant="ghost" size="sm" style={{ color: '#DC2626' }} onClick={() => setDeleteRoom(r)}>Delete</Button>
              </div>
            </div>
          ))}
        </div>
      )}

      <AdminFormDialog isOpen={isAddOpen} onClose={() => setIsAddOpen(false)} title="Add Court Room"
        isLoading={createMutation.isPending} onSubmit={handleSave} submitLabel="Create Room">
        {RoomForm}
      </AdminFormDialog>

      <AdminFormDialog isOpen={!!editRoom} onClose={() => setEditRoom(null)} title="Edit Court Room"
        isLoading={updateMutation.isPending} onSubmit={handleSave} submitLabel="Save Changes">
        {RoomForm}
      </AdminFormDialog>

      <ConfirmDialog
        isOpen={!!deleteRoom}
        onClose={() => setDeleteRoom(null)}
        title="Delete Court Room"
        description={`Delete room "${deleteRoom?.roomNumber}"? This cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteMutation.isPending}
        onConfirm={async () => {
          try {
            await deleteMutation.mutateAsync({ roomUuid: deleteRoom!.uuid, courtUuid: court.uuid })
            toast.success('Room deleted')
            setDeleteRoom(null)
          } catch (err: any) { toast.error(err.message || 'Failed to delete room') }
        }}
      />
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Organization & Judges Panels (Pending Backend)
// ─────────────────────────────────────────────────────────────────────────────

function OrganizationPanel({ court }: { court: CourtResponse }) {
  const { data: benches = [], isLoading } = useCourtBenches(court.uuid)
  
  if (isLoading) return <div style={{ padding: '2rem', color: '#6B7280' }}>Loading organization structure…</div>
  
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      <div style={{ padding: '1rem', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '0.375rem' }}>
        <h4 style={{ margin: '0 0 0.5rem 0', color: '#111827', fontSize: '1rem' }}>{court.courtName}</h4>
        
        {benches.length === 0 ? (
          <div style={{ marginLeft: '1.5rem', color: '#6B7280', fontSize: '0.875rem' }}>No benches defined.</div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', marginLeft: '1.5rem', marginTop: '0.5rem' }}>
            {benches.map((b: BenchResponse) => (
              <div key={b.uuid} style={{ borderLeft: '2px solid #D1D5DB', paddingLeft: '1rem' }}>
                <div style={{ fontWeight: 600, color: '#374151', fontSize: '0.9375rem' }}>
                  Bench: {b.benchNumber} <span style={{ fontWeight: 400, color: '#6B7280', fontSize: '0.8125rem' }}>({b.benchType})</span>
                </div>
                <div style={{ marginLeft: '1rem', marginTop: '0.25rem', color: '#9CA3AF', fontSize: '0.8125rem', fontStyle: 'italic' }}>
                  Court Rooms / Assigned Judges (Pending Backend Support)
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

function JudgesPanel() {
  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
        <h4 style={{ margin: 0, fontSize: '0.9375rem', fontWeight: 600, color: '#111827' }}>Assigned Judges</h4>
        <Button 
          size="sm" 
          onClick={() => toast('Judge-to-Court assignment will be available after the backend Court Organization APIs are implemented.', { icon: 'ℹ️' })}
        >
          + Assign Judge
        </Button>
      </div>
      <div style={{ padding: '2rem', textAlign: 'center', background: '#F9FAFB', border: '1px solid #E5E7EB', borderRadius: '0.375rem' }}>
        <div style={{ color: '#6B7280', fontSize: '0.875rem', marginBottom: '0.5rem' }}>No court assignment data available.</div>
        <div style={{ color: '#9CA3AF', fontSize: '0.75rem' }}>Backend APIs for Court ↔ Judge assignments are currently pending integration.</div>
      </div>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Court Detail Modal
// ─────────────────────────────────────────────────────────────────────────────

function CourtDetailModal({ court, onClose }: { court: CourtResponse; onClose: () => void }) {
  const [activeTab, setActiveTab] = useState<'info' | 'organization' | 'judges' | 'benches' | 'rooms'>('info')

  const tabStyle = (t: string): React.CSSProperties => ({
    padding: '0.5rem 1rem', cursor: 'pointer',
    borderBottom: `2px solid ${activeTab === t ? '#1E40AF' : 'transparent'}`,
    color: activeTab === t ? '#1E40AF' : '#6B7280',
    fontWeight: activeTab === t ? 600 : 400,
    fontSize: '0.875rem', background: 'none', border: 'none',
    transition: 'all 0.15s',
  })

  return (
    <Modal isOpen onClose={onClose} title={court.courtName}>
      <div style={{ width: 720, maxWidth: '95vw', minHeight: 400 }}>
        {/* Tabs */}
        <div style={{ display: 'flex', gap: 0, borderBottom: '1px solid #E5E7EB', padding: '0 1.5rem', overflowX: 'auto' }}>
          <button style={tabStyle('info')} onClick={() => setActiveTab('info')}>Court Info</button>
          <button style={tabStyle('organization')} onClick={() => setActiveTab('organization')}>Organization</button>
          <button style={tabStyle('judges')} onClick={() => setActiveTab('judges')}>Judges</button>
          <button style={tabStyle('benches')} onClick={() => setActiveTab('benches')}>Benches</button>
          <button style={tabStyle('rooms')} onClick={() => setActiveTab('rooms')}>Court Rooms</button>
        </div>

        <div style={{ padding: '1.25rem 1.5rem' }}>
          {activeTab === 'info' && (
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              {[
                ['Court Code',    court.courtCode],
                ['Court Name',    court.courtName],
                ['Court Type',    courtTypeLabel(court.courtType)],
                ['State',         court.state],
                ['District',      court.district],
                ['Phone',         court.phoneNumber || '—'],
                ['Email',         court.email || '—'],
                ['Created',       fmt(court.createdAt)],
                ['Last Updated',  fmt(court.updatedAt)],
              ].map(([k, v]) => (
                <div key={k}>
                  <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: '0.25rem' }}>{k}</div>
                  <div style={{ fontSize: '0.9375rem', color: '#111827' }}>{v}</div>
                </div>
              ))}
              <div style={{ gridColumn: '1 / -1' }}>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: '0.25rem' }}>Address</div>
                <div style={{ fontSize: '0.9375rem', color: '#111827' }}>{court.address || '—'}</div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase', marginBottom: '0.25rem' }}>Status</div>
                <StatusBadge status={court.isActive ? 'active' : 'inactive'} />
              </div>
            </div>
          )}

          {activeTab === 'organization' && <OrganizationPanel court={court} />}
          {activeTab === 'judges' && <JudgesPanel />}
          {activeTab === 'benches' && <BenchPanel court={court} />}
          {activeTab === 'rooms' && <RoomPanel court={court} />}
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', padding: '0.75rem 1.5rem', borderTop: '1px solid #E5E7EB', background: '#F9FAFB' }}>
          <Button variant="ghost" onClick={onClose}>Close</Button>
        </div>
      </div>
    </Modal>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Main Page
// ─────────────────────────────────────────────────────────────────────────────

export function CourtManagementPage() {
  // ── Pagination & Sorting ──────────────────────────────────────────────────
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)
  const [sortKey, setSortKey] = useState(0) // index into SORT_OPTIONS
  const currentSort = SORT_OPTIONS[sortKey]

  // ── Search & Filters ──────────────────────────────────────────────────────
  const [searchInput, setSearchInput] = useState('')
  const [search, setSearch] = useState('')
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const [filterType, setFilterType]     = useState('')
  const [filterStatus, setFilterStatus] = useState('')
  const [filterState, setFilterState]   = useState('')
  const [filterDistrict, setFilterDistrict] = useState('')
  // Applied filters (only take effect when user clicks Apply)
  const [activeFilters, setActiveFilters] = useState({
    type: '', status: '', state: '', district: '',
  })

  // 400ms debounce on search
  const handleSearchChange = (val: string) => {
    setSearchInput(val)
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => {
      setSearch(val)
      setPage(0)
    }, 400)
  }

  useEffect(() => () => { if (debounceRef.current) clearTimeout(debounceRef.current) }, [])

  // ── Data ──────────────────────────────────────────────────────────────────
  const { data: pageData, isLoading, error, refetch } = useAdminCourts(
    page, size, currentSort.value as string, currentSort.dir as 'asc' | 'desc',
  )

  // Client-side search + filter (backend doesn't expose a dedicated court search endpoint)
  const displayedCourts = useMemo(() => {
    let rows: CourtResponse[] = pageData?.content ?? []
    const q = search.trim().toLowerCase()
    if (q) {
      rows = rows.filter(c =>
        c.courtName?.toLowerCase().includes(q) ||
        c.courtCode?.toLowerCase().includes(q) ||
        c.district?.toLowerCase().includes(q) ||
        c.state?.toLowerCase().includes(q)
      )
    }
    if (activeFilters.type)     rows = rows.filter(c => c.courtType === activeFilters.type)
    if (activeFilters.status)   rows = rows.filter(c => (activeFilters.status === 'active') === c.isActive)
    if (activeFilters.state)    rows = rows.filter(c => c.state?.toLowerCase().includes(activeFilters.state.toLowerCase()))
    if (activeFilters.district) rows = rows.filter(c => c.district?.toLowerCase().includes(activeFilters.district.toLowerCase()))
    return rows
  }, [pageData?.content, search, activeFilters])

  // ── RBAC ──────────────────────────────────────────────────────────────────
  const userRole = useSelector(selectUserRole)
  const isAdmin  = ADMIN_ROLES.includes((userRole ?? '') as string)

  // ── Mutations ─────────────────────────────────────────────────────────────
  const createMutation = useCreateCourt()
  const updateMutation = useUpdateCourt()
  const deleteMutation = useDeleteCourt()

  // ── Modal State ───────────────────────────────────────────────────────────
  const [isFormOpen, setIsFormOpen]       = useState(false)
  const [isDeleteOpen, setIsDeleteOpen]   = useState(false)
  const [isDetailOpen, setIsDetailOpen]   = useState(false)
  const [selectedCourt, setSelectedCourt] = useState<CourtResponse | null>(null)
  const [formData, setFormData]           = useState<CourtRequest>(DEFAULT_COURT_FORM)
  const [formError, setFormError]         = useState<string | null>(null)

  const openAdd = () => {
    setSelectedCourt(null)
    setFormData(DEFAULT_COURT_FORM)
    setFormError(null)
    setIsFormOpen(true)
  }

  const openEdit = useCallback((court: CourtResponse) => {
    setSelectedCourt(court)
    setFormData({
      courtCode:   court.courtCode,
      courtName:   court.courtName,
      courtType:   court.courtType,
      state:       court.state,
      district:    court.district,
      address:     court.address,
      phoneNumber: court.phoneNumber ?? '',
      email:       court.email ?? '',
      isActive:    court.isActive,
    })
    setFormError(null)
    setIsFormOpen(true)
  }, [])

  const openDetail = useCallback((court: CourtResponse) => {
    setSelectedCourt(court)
    setIsDetailOpen(true)
  }, [])

  const openDelete = useCallback((court: CourtResponse) => {
    setSelectedCourt(court)
    setIsDeleteOpen(true)
  }, [])

  // ── Form Submit ───────────────────────────────────────────────────────────
  const handleSubmit = async () => {
    setFormError(null)
    if (!formData.courtCode.trim()) { setFormError('Court Code is required.'); return }
    if (!formData.courtName.trim()) { setFormError('Court Name is required.'); return }

    try {
      if (selectedCourt) {
        await updateMutation.mutateAsync({ uuid: selectedCourt.uuid, data: formData })
        toast.success('Court updated successfully')
      } else {
        await createMutation.mutateAsync(formData)
        toast.success('Court created successfully')
      }
      setIsFormOpen(false)
    } catch (err: any) {
      const msg = err.message || 'Operation failed'
      setFormError(msg)
      if (msg.toLowerCase().includes('permission') || msg.toLowerCase().includes('forbidden')) {
        toast.error('Permission Denied: You do not have access to perform this action.')
      } else if (msg.toLowerCase().includes('already exists') || msg.toLowerCase().includes('duplicate')) {
        toast.error(`Conflict: ${msg}`)
      } else {
        toast.error(msg)
      }
    }
  }

  // ── Delete ────────────────────────────────────────────────────────────────
  const handleDelete = async () => {
    if (!selectedCourt) return
    try {
      await deleteMutation.mutateAsync(selectedCourt.uuid)
      toast.success(`Court "${selectedCourt.courtName}" deleted`)
      setIsDeleteOpen(false)
    } catch (err: any) {
      const msg = err.message || 'Delete failed'
      toast.error(msg)
      if (msg.toLowerCase().includes('active')) {
        // Show hint — must deactivate first
        toast('Tip: Deactivate the court before deleting it.', { icon: 'ℹ️', duration: 5000 })
      }
      setIsDeleteOpen(false)
    }
  }

  // ── Table Columns ─────────────────────────────────────────────────────────
  const columns = useMemo(() => [
    {
      id: 'courtCode', header: 'Code', field: 'courtCode',
      renderCell: (val: unknown) => (
        <span style={{ fontFamily: 'monospace', fontSize: '0.8125rem', color: '#374151', fontWeight: 600 }}>{String(val ?? '')}</span>
      ),
    },
    {
      id: 'courtName', header: 'Court Name',
      renderCell: (_val: unknown, row: CourtResponse) => (
        <button
          onClick={() => openDetail(row)}
          style={{
            background: 'none', border: 'none', cursor: 'pointer',
            color: '#1E40AF', fontWeight: 600, fontSize: '0.875rem',
            textAlign: 'left', padding: 0, textDecoration: 'underline dotted',
          }}
        >
          {row.courtName}
        </button>
      ),
    },
    {
      id: 'courtType', header: 'Type',
      renderCell: (_val: unknown, row: CourtResponse) => (
        <span style={{
          display: 'inline-flex', padding: '0.2rem 0.55rem', borderRadius: '9999px',
          fontSize: '0.75rem', fontWeight: 600, background: '#DBEAFE', color: '#1E40AF',
        }}>
          {courtTypeLabel(row.courtType)}
        </span>
      ),
    },
    {
      id: 'location', header: 'Location',
      renderCell: (_val: unknown, row: CourtResponse) => (
        <span style={{ fontSize: '0.875rem', color: '#374151' }}>{row.district}, {row.state}</span>
      ),
    },
    {
      id: 'status', header: 'Status',
      renderCell: (_val: unknown, row: CourtResponse) => (
        <StatusBadge status={row.isActive ? 'active' : 'inactive'} />
      ),
    },
    {
      id: 'createdAt', header: 'Created',
      renderCell: (_val: unknown, row: CourtResponse) => (
        <span style={{ fontSize: '0.8125rem', color: '#6B7280' }}>{fmt(row.createdAt)}</span>
      ),
    },
    {
      id: 'updatedAt', header: 'Updated',
      renderCell: (_val: unknown, row: CourtResponse) => (
        <span style={{ fontSize: '0.8125rem', color: '#6B7280' }}>{fmt(row.updatedAt)}</span>
      ),
    },
    ...(isAdmin ? [{
      id: 'actions', header: 'Actions', align: 'right' as const,
      renderCell: (_val: unknown, row: CourtResponse) => (
        <div style={{ display: 'flex', gap: '0.375rem', justifyContent: 'flex-end' }}>
          <Button variant="ghost" size="sm" onClick={() => openDetail(row)}>View</Button>
          <Button variant="ghost" size="sm" onClick={() => openEdit(row)}>Edit</Button>
          <Button variant="ghost" size="sm" style={{ color: '#DC2626' }} onClick={() => openDelete(row)}>Delete</Button>
        </div>
      ),
    }] : []),
  ], [isAdmin, openDetail, openEdit, openDelete])

  // ── Apply / Clear Filters ─────────────────────────────────────────────────
  const applyFilters = () => {
    setActiveFilters({ type: filterType, status: filterStatus, state: filterState, district: filterDistrict })
    setPage(0)
  }
  const clearFilters = () => {
    setFilterType(''); setFilterStatus(''); setFilterState(''); setFilterDistrict('')
    setActiveFilters({ type: '', status: '', state: '', district: '' })
    setSearch(''); setSearchInput(''); setPage(0)
  }
  const hasActiveFilters = !!(activeFilters.type || activeFilters.status || activeFilters.state || activeFilters.district || search)

  // ── Court Form UI ─────────────────────────────────────────────────────────
  const CourtForm = (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
      <div>
        <label style={lbl}>Court Code *</label>
        <input style={inp} value={formData.courtCode} onChange={e => setFormData(p => ({ ...p, courtCode: e.target.value }))} placeholder="e.g. HC-MH-001" maxLength={20} />
        <span style={{ fontSize: '0.75rem', color: '#9CA3AF' }}>Unique identifier, max 20 characters</span>
      </div>
      <div>
        <label style={lbl}>Court Type</label>
        <select style={inp} value={formData.courtType} onChange={e => setFormData(p => ({ ...p, courtType: e.target.value }))}>
          {COURT_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
        </select>
      </div>

      <div style={{ gridColumn: '1 / -1' }}>
        <label style={lbl}>Court Name *</label>
        <input style={inp} value={formData.courtName} onChange={e => setFormData(p => ({ ...p, courtName: e.target.value }))} placeholder="e.g. High Court of Maharashtra" maxLength={300} />
      </div>

      <div>
        <label style={lbl}>State</label>
        <input style={inp} value={formData.state} onChange={e => setFormData(p => ({ ...p, state: e.target.value }))} placeholder="e.g. Maharashtra" />
      </div>
      <div>
        <label style={lbl}>District</label>
        <input style={inp} value={formData.district} onChange={e => setFormData(p => ({ ...p, district: e.target.value }))} placeholder="e.g. Mumbai" />
      </div>

      <div>
        <label style={lbl}>Phone Number</label>
        <input style={inp} type="tel" value={formData.phoneNumber ?? ''} onChange={e => setFormData(p => ({ ...p, phoneNumber: e.target.value }))} placeholder="+91 22 1234 5678" />
      </div>
      <div>
        <label style={lbl}>Email</label>
        <input style={inp} type="email" value={formData.email ?? ''} onChange={e => setFormData(p => ({ ...p, email: e.target.value }))} placeholder="court@gov.in" />
      </div>

      <div style={{ gridColumn: '1 / -1' }}>
        <label style={lbl}>Address</label>
        <textarea style={inp} rows={2} value={formData.address} onChange={e => setFormData(p => ({ ...p, address: e.target.value }))} placeholder="Full court address" />
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <input type="checkbox" id="court-active" checked={!!formData.isActive} onChange={e => setFormData(p => ({ ...p, isActive: e.target.checked }))} />
        <label htmlFor="court-active" style={{ fontSize: '0.875rem', color: '#374151' }}>Active Court</label>
      </div>
    </div>
  )

  // ─────────────────────────────────────────────────────────────────────────
  return (
    <ContentContainer>
      <PageHeader
        title="Court Management"
        subtitle="Manage courts, benches, and courtrooms across all jurisdictions."
        actions={
          isAdmin ? <Button onClick={openAdd}>+ Add Court</Button> : undefined
        }
      />

      <Section>
        {/* ── Search & Filter Bar ──────────────────────────────────────────── */}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem', marginBottom: '1rem', alignItems: 'flex-end' }}>
          {/* Search */}
          <div style={{ flex: '1 1 220px', minWidth: 180 }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Search</label>
            <input
              style={{ ...inp, paddingLeft: '0.75rem' }}
              value={searchInput}
              onChange={e => handleSearchChange(e.target.value)}
              placeholder="Court name, code, district, state…"
            />
          </div>

          {/* Court Type Filter */}
          <div style={{ flex: '0 1 180px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Court Type</label>
            <select style={inp} value={filterType} onChange={e => setFilterType(e.target.value)}>
              <option value="">All Types</option>
              {COURT_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
            </select>
          </div>

          {/* Status Filter */}
          <div style={{ flex: '0 1 140px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Status</label>
            <select style={inp} value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
              <option value="">All</option>
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </select>
          </div>

          {/* Sort */}
          <div style={{ flex: '0 1 200px' }}>
            <label style={{ ...lbl, marginBottom: '0.25rem' }}>Sort By</label>
            <select style={inp} value={sortKey} onChange={e => { setSortKey(Number(e.target.value)); setPage(0) }}>
              {SORT_OPTIONS.map((opt, i) => (
                <option key={i} value={i}>{opt.label}</option>
              ))}
            </select>
          </div>

          {/* Apply / Clear */}
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <Button variant="primary" size="sm" onClick={applyFilters}>Apply Filters</Button>
            {hasActiveFilters && (
              <Button variant="ghost" size="sm" onClick={clearFilters}>Clear All</Button>
            )}
          </div>
        </div>

        {/* Active Filter Chips */}
        {hasActiveFilters && (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.375rem', marginBottom: '0.75rem' }}>
            {search && (
              <span style={{ padding: '0.2rem 0.5rem', borderRadius: '9999px', background: '#DBEAFE', color: '#1E40AF', fontSize: '0.75rem', fontWeight: 600 }}>
                Search: {search}
              </span>
            )}
            {activeFilters.type && (
              <span style={{ padding: '0.2rem 0.5rem', borderRadius: '9999px', background: '#FEF3C7', color: '#92400E', fontSize: '0.75rem', fontWeight: 600 }}>
                Type: {courtTypeLabel(activeFilters.type)}
              </span>
            )}
            {activeFilters.status && (
              <span style={{ padding: '0.2rem 0.5rem', borderRadius: '9999px', background: '#D1FAE5', color: '#065F46', fontSize: '0.75rem', fontWeight: 600 }}>
                Status: {activeFilters.status}
              </span>
            )}
          </div>
        )}

        {/* ── Data Table ──────────────────────────────────────────────────── */}
        <AdminTable
          data={displayedCourts as any[]}
          columns={columns}
          rowId="uuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage={hasActiveFilters ? 'No courts match your search/filter criteria.' : 'No courts found. Add the first court.'}
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

      {/* ── Add / Edit Court Dialog ────────────────────────────────────────── */}
      <AdminFormDialog
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        title={selectedCourt ? `Edit Court — ${selectedCourt.courtCode}` : 'Add New Court'}
        isLoading={createMutation.isPending || updateMutation.isPending}
        onSubmit={handleSubmit}
        submitLabel={selectedCourt ? 'Save Changes' : 'Create Court'}
        error={formError}
        size="lg"
      >
        {CourtForm}
      </AdminFormDialog>

      {/* ── Delete Confirmation ────────────────────────────────────────────── */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        title="Delete Court"
        description={
          selectedCourt?.isActive
            ? `"${selectedCourt?.courtName}" is currently active. You must deactivate it before it can be deleted.`
            : `Permanently delete "${selectedCourt?.courtName}"? All associated benches and rooms will also be removed. This action cannot be undone.`
        }
        confirmLabel={selectedCourt?.isActive ? 'Understood' : 'Delete Court'}
        isLoading={deleteMutation.isPending}
        onConfirm={selectedCourt?.isActive ? () => setIsDeleteOpen(false) : handleDelete}
      />

      {/* ── Court Detail Modal ─────────────────────────────────────────────── */}
      {isDetailOpen && selectedCourt && (
        <CourtDetailModal court={selectedCourt} onClose={() => setIsDetailOpen(false)} />
      )}
    </ContentContainer>
  )
}
