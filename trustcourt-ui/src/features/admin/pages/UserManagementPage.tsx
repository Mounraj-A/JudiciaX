// ─── UserManagementPage — Phase F10 ──────────────────────────────────────────
import React, { useState } from 'react'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { AdminTable } from '../components/AdminTable'
import { AdminFormDialog, ConfirmDialog } from '../components/AdminFormDialog'
import { useAdminUsers, useApproveUser, useRejectUser, useLockUser, useUnlockUser, useDeleteUser, useAssignRole, useCreateUser } from '../hooks/useAdminUsers'
import { useAdminRoles } from '../hooks/useAdminRoles'
import { toast } from 'react-hot-toast'
import type { UserRole } from '../api/adminUserApi'
import { Copy } from 'lucide-react'

export function UserManagementPage() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)
  
  const [roleFilter, setRoleFilter] = useState('')
  const [searchQuery, setSearchQuery] = useState('')
  const [sortBy, setSortBy] = useState('newest')
  
  const { data: responseData, isLoading, error, refetch } = useAdminUsers(page, size)
  
  const filteredUsers = React.useMemo(() => {
    const pageData = responseData; // responseData is already PageResponse
    if (!pageData?.content) return []
    let users = pageData.content
    
    // Local role filtering
    if (roleFilter) {
      users = users.filter((u: any) => u.role === roleFilter)
    }
    
    if (searchQuery) {
      const q = searchQuery.toLowerCase()
      users = users.filter((u: any) => 
        u.fullName?.toLowerCase().includes(q) || 
        u.username?.toLowerCase().includes(q) || 
        u.email?.toLowerCase().includes(q)
      )
    }

    if (sortBy === 'username-asc') {
      users = [...users].sort((a: any, b: any) => (a.username || '').localeCompare(b.username || ''))
    } else if (sortBy === 'email-asc') {
      users = [...users].sort((a: any, b: any) => (a.email || '').localeCompare(b.email || ''))
    }
    
    return users
  }, [responseData?.content, searchQuery, roleFilter, sortBy])
  const { data: rolesData } = useAdminRoles()
  
  const rejectMutation = useRejectUser()
  const lockMutation = useLockUser()
  const unlockMutation = useUnlockUser()
  const deleteMutation = useDeleteUser()
  const assignRoleMutation = useAssignRole()
  const createMutation = useCreateUser()

  // State for modals
  const [selectedUser, setSelectedUser] = useState<any>(null)
  const [isRejectModalOpen, setIsRejectModalOpen] = useState(false)
  const [isLockModalOpen, setIsLockModalOpen] = useState(false)
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false)
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false)
  const [isAddModalOpen, setIsAddModalOpen] = useState(false)
  const [addForm, setAddForm] = useState({ fullName: '', phone: '', email: '', password: '', role: 'VIEWER' })
  
  const [reason, setReason] = useState('')
  const [selectedRole, setSelectedRole] = useState<UserRole>('VIEWER')

  const columns = [
    { id: 'sno', header: 'S.No', renderCell: (_val: any, _row: any, idx: number) => page * size + idx + 1, width: 60 },
    { 
      id: 'uuid', 
      header: 'UUID', 
      renderCell: (_val: any, row: any) => (
        <span 
          style={{ fontSize: '0.75rem', fontFamily: 'monospace', cursor: 'pointer', color: '#2563EB', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
          onClick={() => {
            navigator.clipboard.writeText(row.uuid)
            toast.success('UUID copied to clipboard')
          }}
          title="Click to copy full UUID"
        >
          {row.uuid.substring(0, 8)}...
          <Copy size={12} />
        </span>
      ),
      width: 100
    },
    { id: 'username', header: 'Username', field: 'username' },
    { id: 'email', header: 'Email', field: 'email' },
    { id: 'mobile', header: 'Mobile', renderCell: (_val: any, row: any) => row.phoneNumber || 'N/A' },
    { id: 'role', header: 'Role', field: 'role' },
    {
      id: 'status', header: 'Status',
      renderCell: (_val: any, row: any) => {
        if (row.isDeleted) return <StatusBadge status="deleted" label="Deleted" />
        if (row.isLocked || row.accountStatus === 'LOCKED') return <StatusBadge status="locked" label="Locked" />
        return <StatusBadge status={row.accountStatus?.toLowerCase() || 'info'} label={row.accountStatus || 'UNKNOWN'} />
      }
    },
    {
      id: 'actions', header: 'Actions',
      renderCell: (_val: any, row: any) => {
        if (row.isDeleted) return <span style={{ fontSize: '0.75rem', color: '#6B7280' }}>Deleted</span>
        
        return (
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <Button 
              variant="outline" 
              size="sm" 
              onClick={() => {
                setSelectedUser(row)
                setSelectedRole(row.role || 'ROLE_VIEWER')
                setIsRoleModalOpen(true)
              }}
            >
              Role
            </Button>
            
            {row.isLocked || row.accountStatus === 'LOCKED' ? (
              <Button 
                variant="outline" 
                size="sm"
                onClick={async () => {
                  try {
                    await unlockMutation.mutateAsync(row.uuid)
                    toast.success('User unlocked')
                  } catch (err: any) {
                    toast.error(err.message)
                  }
                }}
              >
                Unlock
              </Button>
            ) : (
              <Button 
                variant="outline" 
                size="sm"
                onClick={() => {
                  setSelectedUser(row)
                  setIsLockModalOpen(true)
                }}
              >
                Lock
              </Button>
            )}

            <Button 
              variant="destructive" 
              size="sm"
              onClick={() => {
                setSelectedUser(row)
                setIsDeleteModalOpen(true)
              }}
            >
              Delete
            </Button>
          </div>
        )
      }
    }
  ]

  return (
    <ContentContainer maxWidth="100%">
      <PageHeader
        title="User Management"
        subtitle="Manage platform access, roles, and account statuses."
        actions={
          <Button onClick={() => setIsAddModalOpen(true)}>Add User</Button>
        }
      />

      <Section>
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
          <input 
            type="text" 
            placeholder="Search users..." 
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{ padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB', flex: 1, maxWidth: '300px' }}
          />
          <select 
            value={roleFilter} 
            onChange={(e) => { setRoleFilter(e.target.value); setPage(0); }}
            style={{ padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
          >
            <option value="">All Roles</option>
            <option value="ADMIN">Admin</option>
            <option value="JUDGE">Judge</option>
            <option value="CLERK">Clerk</option>
            <option value="ADVOCATE">Advocate</option>
            <option value="VIEWER">Viewer</option>
          </select>
          <select 
            value={sortBy} 
            onChange={(e) => { setSortBy(e.target.value); }}
            style={{ padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
          >
            <option value="newest">Sort by Newest</option>
            <option value="username-asc">Username (A-Z)</option>
            <option value="email-asc">Email (A-Z)</option>
          </select>
        </div>

        <AdminTable
          data={filteredUsers}
          columns={columns}
          rowId="uuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage="No users found."
          onRefresh={() => refetch()}
          pagination={{
            page: responseData?.number || 0,
            size: responseData?.size || 20,
            totalElements: responseData?.totalElements || 0,
            totalPages: responseData?.totalPages || 0,
            onPageChange: setPage,
            onSizeChange: (s) => { setSize(s); setPage(0) }
          }}
        />
      </Section>

      {/* Add User Modal */}
      <AdminFormDialog
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        title="Add New User"
        submitLabel="Create User"
        isLoading={createMutation.isPending}
        onSubmit={async () => {
          try {
            await createMutation.mutateAsync(addForm)
            toast.success('User created')
            setIsAddModalOpen(false)
            setAddForm({ fullName: '', phone: '', email: '', password: '', role: 'VIEWER' })
          } catch (err: any) {
            toast.error(err.message || 'Failed to create user')
          }
        }}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Full Name</label>
            <input type="text" value={addForm.fullName} onChange={e => setAddForm(p => ({...p, fullName: e.target.value}))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Phone Number</label>
            <input type="text" value={addForm.phone} onChange={e => setAddForm(p => ({...p, phone: e.target.value}))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Email</label>
            <input type="email" value={addForm.email} onChange={e => setAddForm(p => ({...p, email: e.target.value}))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Password</label>
            <input type="password" value={addForm.password} onChange={e => setAddForm(p => ({...p, password: e.target.value}))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }} />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500 }}>Role</label>
            <select value={addForm.role} onChange={e => setAddForm(p => ({...p, role: e.target.value}))} style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}>
              {['ADMIN', 'JUDGE', 'CLERK', 'ADVOCATE', 'VIEWER'].map(r => (
                <option key={r} value={r}>{r}</option>
              ))}
            </select>
          </div>
        </div>
      </AdminFormDialog>

      {/* Reject Modal */}
      <AdminFormDialog
        isOpen={isRejectModalOpen}
        onClose={() => setIsRejectModalOpen(false)}
        title={`Reject ${selectedUser?.fullName}`}
        submitLabel="Reject User"
        isDestructive
        isLoading={rejectMutation.isPending}
        onSubmit={async () => {
          try {
            await rejectMutation.mutateAsync({ uuid: selectedUser.uuid, reason })
            toast.success('User rejected')
            setIsRejectModalOpen(false)
          } catch (err: any) {
            toast.error(err.message)
          }
        }}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151' }}>Reason for Rejection</label>
          <textarea
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
            rows={3}
            placeholder="e.g., Identity verification failed"
          />
        </div>
      </AdminFormDialog>

      {/* Lock Modal */}
      <AdminFormDialog
        isOpen={isLockModalOpen}
        onClose={() => setIsLockModalOpen(false)}
        title={`Lock ${selectedUser?.fullName}`}
        submitLabel="Lock Account"
        isDestructive
        isLoading={lockMutation.isPending}
        onSubmit={async () => {
          try {
            await lockMutation.mutateAsync({ uuid: selectedUser.uuid, reason: reason || 'Suspicious activity' })
            toast.success('User locked')
            setIsLockModalOpen(false)
          } catch (err: any) {
            toast.error(err.message)
          }
        }}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151' }}>Reason for Locking</label>
          <textarea
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
            rows={3}
            placeholder="Suspicious activity"
          />
        </div>
      </AdminFormDialog>

      {/* Assign Role Modal */}
      <AdminFormDialog
        isOpen={isRoleModalOpen}
        onClose={() => setIsRoleModalOpen(false)}
        title={`Assign Role for ${selectedUser?.fullName}`}
        submitLabel="Assign Role"
        isLoading={assignRoleMutation.isPending}
        onSubmit={async () => {
          try {
            await assignRoleMutation.mutateAsync({ uuid: selectedUser.uuid, role: selectedRole })
            toast.success('Role assigned')
            setIsRoleModalOpen(false)
          } catch (err: any) {
            toast.error(err.message)
          }
        }}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151' }}>Select Role</label>
          <select
            value={selectedRole}
            onChange={(e) => setSelectedRole(e.target.value as UserRole)}
            style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
          >
            {(rolesData && rolesData.length > 0) ? rolesData.map((r: any) => (
              <option key={r.uuid} value={r.roleCode}>{r.displayName}</option>
            )) : ['ROLE_ADMIN', 'ROLE_JUDGE', 'ROLE_CLERK', 'ROLE_ADVOCATE', 'ROLE_VIEWER'].map(r => (
              <option key={r} value={r}>{r.replace('ROLE_', '')}</option>
            ))}
          </select>
        </div>
      </AdminFormDialog>

      {/* Delete Confirm */}
      <ConfirmDialog
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="Delete User"
        description={`Are you sure you want to completely delete ${selectedUser?.fullName}? This action cannot be undone.`}
        confirmLabel="Delete User"
        isLoading={deleteMutation.isPending}
        onConfirm={async () => {
          try {
            await deleteMutation.mutateAsync(selectedUser.uuid)
            toast.success('User deleted')
            setIsDeleteModalOpen(false)
          } catch (err: any) {
            toast.error(err.message)
          }
        }}
      />
    </ContentContainer>
  )
}
