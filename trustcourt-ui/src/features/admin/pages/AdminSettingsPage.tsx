// ─── AdminSettingsPage — Phase F10 ───────────────────────────────────────────
import React, { useState } from 'react'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge } from '@/shared/components/badges'
import { AdminTable } from '../components/AdminTable'
import { AdminFormDialog, ConfirmDialog } from '../components/AdminFormDialog'
import { useAdminConfigurations, useUpdateConfiguration } from '../hooks/useAdminConfig'
import { useAdminAnnouncements, useCreateAnnouncement, usePublishAnnouncement, useArchiveAnnouncement, useDeleteAnnouncement } from '../hooks/useAdminAnnouncements'
import { useAdminMaintenance, useCreateMaintenance, useActivateMaintenance, useCompleteMaintenance, useCancelMaintenance, useDeleteMaintenance } from '../hooks/useAdminMaintenance'
import { toast } from 'react-hot-toast'
import type { ConfigurationRequest } from '../api/adminConfigApi'
import type { AnnouncementRequest } from '../api/adminAnnouncementApi'
import type { MaintenanceRequest } from '../api/adminMaintenanceApi'

export function AdminSettingsPage() {
  const [activeTab, setActiveTab] = useState<'config' | 'announcements' | 'maintenance'>('config')

  // Config State
  const { data: configs, isLoading: loadingConfigs, refetch: refetchConfigs } = useAdminConfigurations()
  const updateConfigMutation = useUpdateConfiguration()
  const [isConfigModalOpen, setIsConfigModalOpen] = useState(false)
  const [selectedConfig, setSelectedConfig] = useState<any>(null)
  const [configValue, setConfigValue] = useState('')

  // Announcement State
  const { data: annData, isLoading: loadingAnn, refetch: refetchAnn } = useAdminAnnouncements(0, 50)
  const createAnnMutation = useCreateAnnouncement()
  const publishAnnMutation = usePublishAnnouncement()
  const archiveAnnMutation = useArchiveAnnouncement()
  const deleteAnnMutation = useDeleteAnnouncement()
  const [isAnnModalOpen, setIsAnnModalOpen] = useState(false)
  const [annForm, setAnnForm] = useState<AnnouncementRequest>({ title: '', content: '', targetRoles: ['ALL'], priority: 'LOW' })

  // Maintenance State
  const { data: maintData, isLoading: loadingMaint, refetch: refetchMaint } = useAdminMaintenance()
  const createMaintMutation = useCreateMaintenance()
  const activateMaintMutation = useActivateMaintenance()
  const completeMaintMutation = useCompleteMaintenance()
  const cancelMaintMutation = useCancelMaintenance()
  const deleteMaintMutation = useDeleteMaintenance()
  const [isMaintModalOpen, setIsMaintModalOpen] = useState(false)
  const [maintForm, setMaintForm] = useState<MaintenanceRequest>({ title: '', description: '', scheduledStart: '', scheduledEnd: '', affectedServices: ['ALL'] })

  // ── Render Helpers ─────────────────────────────────────────────────────────
  const renderConfig = () => {
    const columns = [
      { id: 'key', header: 'Key', field: 'configKey' },
      { id: 'value', header: 'Value', renderCell: (val: any, row: any) => row.isEncrypted ? '********' : row.configValue },
      { id: 'desc', header: 'Description', field: 'description' },
      { id: 'status', header: 'Status', renderCell: (val: any, row: any) => <StatusBadge status={row.isActive ? 'success' : 'secondary'} label={row.isActive ? 'Active' : 'Inactive'} /> },
      {
        id: 'actions', header: 'Actions', align: 'right',
        renderCell: (val: any, row: any) => (
          <Button variant="ghost" onClick={() => { setSelectedConfig(row); setConfigValue(row.configValue); setIsConfigModalOpen(true) }}>Edit</Button>
        )
      }
    ]
    return (
      <>
        <AdminTable data={Array.isArray(configs) ? configs : (configs as any)?.content || []} columns={columns} rowId="uuid" loading={loadingConfigs} onRefresh={() => refetchConfigs()} />
        <AdminFormDialog
          isOpen={isConfigModalOpen}
          onClose={() => setIsConfigModalOpen(false)}
          title={`Edit ${selectedConfig?.configKey}`}
          isLoading={updateConfigMutation.isPending}
          onSubmit={async () => {
            try {
              await updateConfigMutation.mutateAsync({ uuid: selectedConfig.uuid, data: { ...selectedConfig, configValue } })
              toast.success('Configuration updated')
              setIsConfigModalOpen(false)
            } catch (err: any) { toast.error(err.message) }
          }}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>New Value</label>
            <input type={selectedConfig?.isEncrypted ? 'password' : 'text'} value={configValue} onChange={(e) => setConfigValue(e.target.value)} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
        </AdminFormDialog>
      </>
    )
  }

  const renderAnnouncements = () => {
    const columns = [
      { id: 'title', header: 'Title', field: 'title' },
      { id: 'roles', header: 'Target Roles', renderCell: (val: any, row: any) => row.targetRoles.join(', ') },
      { id: 'status', header: 'Status', renderCell: (val: any, row: any) => <StatusBadge status={row.status === 'PUBLISHED' ? 'success' : row.status === 'DRAFT' ? 'info' : 'secondary'} label={row.status} /> },
      { id: 'priority', header: 'Priority', renderCell: (val: any, row: any) => <StatusBadge status={row.priority === 'HIGH' || row.priority === 'CRITICAL' ? 'danger' : 'secondary'} label={row.priority} /> },
      {
        id: 'actions', header: 'Actions', align: 'right',
        renderCell: (val: any, row: any) => (
          <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
            {row.status === 'DRAFT' && <Button variant="ghost" onClick={async () => { try { await publishAnnMutation.mutateAsync(row.uuid); toast.success('Published'); } catch (e:any) { toast.error(e.message) } }}>Publish</Button>}
            {row.status === 'PUBLISHED' && <Button variant="ghost" onClick={async () => { try { await archiveAnnMutation.mutateAsync(row.uuid); toast.success('Archived'); } catch (e:any) { toast.error(e.message) } }}>Archive</Button>}
            <Button variant="ghost" style={{ color: '#DC2626' }} onClick={async () => { try { await deleteAnnMutation.mutateAsync(row.uuid); toast.success('Deleted'); } catch (e:any) { toast.error(e.message) } }}>Delete</Button>
          </div>
        )
      }
    ]
    return (
      <>
        <div style={{ marginBottom: '1rem', display: 'flex', justifyContent: 'flex-end' }}>
          <Button onClick={() => { setAnnForm({ title: '', content: '', targetRoles: ['ALL'], priority: 'LOW' }); setIsAnnModalOpen(true); }}>New Announcement</Button>
        </div>
        <AdminTable data={Array.isArray(annData) ? annData : (annData as any)?.content || []} columns={columns} rowId="uuid" loading={loadingAnn} onRefresh={() => refetchAnn()} />
        <AdminFormDialog
          isOpen={isAnnModalOpen}
          onClose={() => setIsAnnModalOpen(false)}
          title="Create Announcement"
          isLoading={createAnnMutation.isPending}
          onSubmit={async () => {
            try {
              await createAnnMutation.mutateAsync(annForm)
              toast.success('Announcement created as Draft')
              setIsAnnModalOpen(false)
            } catch (err: any) { toast.error(err.message) }
          }}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Title</label>
              <input type="text" value={annForm.title} onChange={(e) => setAnnForm(p => ({ ...p, title: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
            </div>
            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Content</label>
              <textarea value={annForm.content} onChange={(e) => setAnnForm(p => ({ ...p, content: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} rows={3} />
            </div>
            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Priority</label>
              <select value={annForm.priority} onChange={(e) => setAnnForm(p => ({ ...p, priority: e.target.value as any }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}>
                <option value="LOW">Low</option><option value="MEDIUM">Medium</option><option value="HIGH">High</option><option value="CRITICAL">Critical</option>
              </select>
            </div>
          </div>
        </AdminFormDialog>
      </>
    )
  }

  const renderMaintenance = () => {
    const columns = [
      { id: 'title', header: 'Title', field: 'title' },
      { id: 'status', header: 'Status', renderCell: (val: any, row: any) => <StatusBadge status={row.status === 'ACTIVE' ? 'warning' : row.status === 'COMPLETED' ? 'success' : row.status === 'CANCELLED' ? 'secondary' : 'info'} label={row.status} /> },
      { id: 'start', header: 'Scheduled Start', field: 'scheduledStart' },
      { id: 'end', header: 'Scheduled End', field: 'scheduledEnd' },
      {
        id: 'actions', header: 'Actions', align: 'right',
        renderCell: (val: any, row: any) => (
          <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
            {row.status === 'SCHEDULED' && <Button variant="ghost" onClick={async () => { try { await activateMaintMutation.mutateAsync(row.uuid); toast.success('Activated'); } catch (e:any) { toast.error(e.message) } }}>Activate</Button>}
            {row.status === 'ACTIVE' && <Button variant="ghost" onClick={async () => { try { await completeMaintMutation.mutateAsync(row.uuid); toast.success('Completed'); } catch (e:any) { toast.error(e.message) } }}>Complete</Button>}
            {(row.status === 'SCHEDULED' || row.status === 'ACTIVE') && <Button variant="ghost" style={{ color: '#DC2626' }} onClick={async () => { try { await cancelMaintMutation.mutateAsync(row.uuid); toast.success('Cancelled'); } catch (e:any) { toast.error(e.message) } }}>Cancel</Button>}
            {row.status !== 'ACTIVE' && <Button variant="ghost" style={{ color: '#DC2626' }} onClick={async () => { try { await deleteMaintMutation.mutateAsync(row.uuid); toast.success('Deleted'); } catch (e:any) { toast.error(e.message) } }}>Delete</Button>}
          </div>
        )
      }
    ]
    return (
      <>
        <div style={{ marginBottom: '1rem', display: 'flex', justifyContent: 'flex-end' }}>
          <Button onClick={() => { setMaintForm({ title: '', description: '', scheduledStart: '', scheduledEnd: '', affectedServices: ['ALL'] }); setIsMaintModalOpen(true); }}>Schedule Maintenance</Button>
        </div>
        <AdminTable data={Array.isArray(maintData) ? maintData : (maintData as any)?.content || []} columns={columns} rowId="uuid" loading={loadingMaint} onRefresh={() => refetchMaint()} />
        <AdminFormDialog
          isOpen={isMaintModalOpen}
          onClose={() => setIsMaintModalOpen(false)}
          title="Schedule Maintenance"
          isLoading={createMaintMutation.isPending}
          onSubmit={async () => {
            try {
              await createMaintMutation.mutateAsync(maintForm)
              toast.success('Maintenance scheduled')
              setIsMaintModalOpen(false)
            } catch (err: any) { toast.error(err.message) }
          }}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Title</label>
              <input type="text" value={maintForm.title} onChange={(e) => setMaintForm(p => ({ ...p, title: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
            </div>
            <div>
              <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Description</label>
              <textarea value={maintForm.description} onChange={(e) => setMaintForm(p => ({ ...p, description: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} rows={2} />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Start Time (ISO)</label>
                <input type="text" value={maintForm.scheduledStart} onChange={(e) => setMaintForm(p => ({ ...p, scheduledStart: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} placeholder="YYYY-MM-DDTHH:mm:ssZ" />
              </div>
              <div>
                <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>End Time (ISO)</label>
                <input type="text" value={maintForm.scheduledEnd} onChange={(e) => setMaintForm(p => ({ ...p, scheduledEnd: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} placeholder="YYYY-MM-DDTHH:mm:ssZ" />
              </div>
            </div>
          </div>
        </AdminFormDialog>
      </>
    )
  }

  return (
    <ContentContainer>
      <PageHeader
        title="Platform Settings"
        subtitle="Manage global configurations, system announcements, and maintenance windows."
      />

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid #E5E7EB', marginBottom: '1.5rem', paddingBottom: '0.5rem' }}>
        {[
          { id: 'config', label: 'System Configuration' },
          { id: 'announcements', label: 'Announcements' },
          { id: 'maintenance', label: 'Maintenance Windows' }
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id as any)}
            style={{
              padding: '0.5rem 1rem', fontSize: '0.875rem', fontWeight: 600,
              color: activeTab === tab.id ? '#0F1D3A' : '#6B7280',
              borderBottom: activeTab === tab.id ? '2px solid #0F1D3A' : 'none',
              background: 'none', cursor: 'pointer'
            }}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <Section>
        {activeTab === 'config' && renderConfig()}
        {activeTab === 'announcements' && renderAnnouncements()}
        {activeTab === 'maintenance' && renderMaintenance()}
      </Section>
    </ContentContainer>
  )
}
