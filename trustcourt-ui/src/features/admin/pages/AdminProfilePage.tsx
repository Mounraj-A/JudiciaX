// ─── AdminProfilePage — Phase F10 ────────────────────────────────────────────
import React, { useState } from 'react'
import { PageHeader, ContentContainer, Section, GridLayout } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { StatusBadge } from '@/shared/components/badges'
import { useMyProfile, useUpdateMyProfile, useChangePassword, useMySessions, useRevokeMySession, useRevokeAllMySessions } from '../hooks/useAdminProfile'
import { toast } from 'react-hot-toast'
import { LoadingLayout, ErrorLayout } from '@/shared/components/layout/StateLayouts'
import { AdminTable } from '../components/AdminTable'
import { AdminFormDialog } from '../components/AdminFormDialog'
import { useAuthContext } from '@/features/auth/contexts/AuthContext'
import { LogOut } from 'lucide-react'

export function AdminProfilePage() {
  const { data: profile, isLoading: loadingProfile, error: profileError, refetch: refetchProfile } = useMyProfile()
  const { data: sessions, isLoading: loadingSessions, refetch: refetchSessions } = useMySessions()
  const { logout } = useAuthContext()

  const updateProfileMutation = useUpdateMyProfile()
  const changePasswordMutation = useChangePassword()
  const revokeSessionMutation = useRevokeMySession()
  const revokeAllSessionsMutation = useRevokeAllMySessions()

  const [activeTab, setActiveTab] = useState<'details' | 'security' | 'sessions'>('details')

  const [isEditProfileOpen, setIsEditProfileOpen] = useState(false)
  const [profileForm, setProfileForm] = useState({ firstName: '', lastName: '', mobile: '' })

  const [isPasswordOpen, setIsPasswordOpen] = useState(false)
  const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' })

  if (loadingProfile) return <LoadingLayout />
  if (profileError) return <ErrorLayout message={profileError.message} action={<Button onClick={() => refetchProfile()}>Retry</Button>} />
  if (!profile) return null

  const handleOpenEdit = () => {
    setProfileForm({ firstName: profile.firstName, lastName: profile.lastName, mobile: profile.mobile || '' })
    setIsEditProfileOpen(true)
  }

  const handleUpdateProfile = async () => {
    try {
      await updateProfileMutation.mutateAsync(profileForm)
      toast.success('Profile updated')
      setIsEditProfileOpen(false)
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const handleChangePassword = async () => {
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      toast.error('Passwords do not match')
      return
    }
    try {
      await changePasswordMutation.mutateAsync(passwordForm)
      toast.success('Password changed successfully')
      setIsPasswordOpen(false)
      setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' })
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  const sessionColumns = [
    { id: 'ip', header: 'IP Address', field: 'ipAddress' },
    { id: 'device', header: 'Device', field: 'deviceType' },
    { id: 'created', header: 'Created', field: 'createdAt' },
    {
      id: 'status', header: 'Status',
      renderCell: (val: any, row: any) => row.isCurrentSession ? <StatusBadge status="success" label="Current" /> : <StatusBadge status="info" label="Active" />
    },
    {
      id: 'actions', header: 'Actions', align: 'right',
      renderCell: (val: any, row: any) => (
        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
          {!row.isCurrentSession && (
            <Button variant="ghost" style={{ color: '#DC2626' }} onClick={async () => {
              try { await revokeSessionMutation.mutateAsync(row.sessionUuid); toast.success('Session revoked') }
              catch (e:any) { toast.error(e.message) }
            }}>Revoke</Button>
          )}
        </div>
      )
    }
  ]

  return (
    <ContentContainer>
      <PageHeader
        title="My Profile"
        subtitle="Manage your personal details, security settings, and active sessions."
      />

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid #E5E7EB', marginBottom: '1.5rem', paddingBottom: '0.5rem' }}>
        {[
          { id: 'details', label: 'Personal Details' },
          { id: 'security', label: 'Security' },
          { id: 'sessions', label: 'Active Sessions' }
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
        {activeTab === 'details' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1.5rem', background: '#F9FAFB', borderRadius: '0.5rem', border: '1px solid #E5E7EB' }}>
              <div>
                <div style={{ fontSize: '1.25rem', fontWeight: 600, color: '#111827' }}>{profile.fullName}</div>
                <div style={{ color: '#6B7280', marginTop: '0.25rem' }}>{profile.email} · {profile.role}</div>
              </div>
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <Button onClick={handleOpenEdit}>Edit Profile</Button>
                <Button variant="destructive" onClick={logout} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <LogOut size={16} /> Logout
                </Button>
              </div>
            </div>
            <GridLayout cols={2}>
              <div>
                <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase' }}>Username</label>
                <div style={{ fontSize: '1rem', fontWeight: 500, marginTop: '0.25rem' }}>{profile.username}</div>
              </div>
              <div>
                <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase' }}>Mobile</label>
                <div style={{ fontSize: '1rem', fontWeight: 500, marginTop: '0.25rem' }}>{profile.mobile || 'Not provided'}</div>
              </div>
              <div>
                <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase' }}>Account Status</label>
                <div style={{ marginTop: '0.25rem' }}><StatusBadge status={profile.accountStatus === 'ACTIVE' ? 'success' : 'warning'} label={profile.accountStatus} /></div>
              </div>
            </GridLayout>
          </div>
        )}

        {activeTab === 'security' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1.5rem', background: '#F9FAFB', borderRadius: '0.5rem', border: '1px solid #E5E7EB' }}>
              <div>
                <div style={{ fontSize: '1.125rem', fontWeight: 600, color: '#111827' }}>Change Password</div>
                <div style={{ color: '#6B7280', marginTop: '0.25rem', fontSize: '0.875rem' }}>Update your password to keep your account secure.</div>
              </div>
              <Button onClick={() => setIsPasswordOpen(true)}>Update Password</Button>
            </div>
          </div>
        )}

        {activeTab === 'sessions' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <h3 style={{ fontSize: '1.125rem', fontWeight: 600 }}>Active Sessions</h3>
              <Button variant="destructive" onClick={async () => {
                try { await revokeAllSessionsMutation.mutateAsync(); toast.success('All sessions revoked'); }
                catch (e:any) { toast.error(e.message) }
              }}>Revoke All</Button>
            </div>
            <AdminTable
              data={sessions || []}
              columns={sessionColumns}
              rowId="sessionUuid"
              loading={loadingSessions}
              onRefresh={() => refetchSessions()}
            />
          </div>
        )}
      </Section>

      <AdminFormDialog
        isOpen={isEditProfileOpen}
        onClose={() => setIsEditProfileOpen(false)}
        title="Edit Profile"
        isLoading={updateProfileMutation.isPending}
        onSubmit={handleUpdateProfile}
      >
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>First Name</label>
            <input type="text" value={profileForm.firstName} onChange={(e) => setProfileForm(p => ({ ...p, firstName: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Last Name</label>
            <input type="text" value={profileForm.lastName} onChange={(e) => setProfileForm(p => ({ ...p, lastName: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div style={{ gridColumn: '1 / -1' }}>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Mobile Number</label>
            <input type="text" value={profileForm.mobile} onChange={(e) => setProfileForm(p => ({ ...p, mobile: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
        </div>
      </AdminFormDialog>

      <AdminFormDialog
        isOpen={isPasswordOpen}
        onClose={() => setIsPasswordOpen(false)}
        title="Change Password"
        isLoading={changePasswordMutation.isPending}
        onSubmit={handleChangePassword}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Current Password</label>
            <input type="password" value={passwordForm.currentPassword} onChange={(e) => setPasswordForm(p => ({ ...p, currentPassword: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>New Password</label>
            <input type="password" value={passwordForm.newPassword} onChange={(e) => setPasswordForm(p => ({ ...p, newPassword: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Confirm New Password</label>
            <input type="password" value={passwordForm.confirmPassword} onChange={(e) => setPasswordForm(p => ({ ...p, confirmPassword: e.target.value }))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
        </div>
      </AdminFormDialog>
    </ContentContainer>
  )
}
