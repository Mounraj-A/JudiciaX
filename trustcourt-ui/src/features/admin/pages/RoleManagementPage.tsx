// â”€â”€â”€ RoleManagementPage â€” Phase F10 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import { useState } from 'react'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { AdminTable } from '../components/AdminTable'
import { AdminFormDialog, ConfirmDialog } from '../components/AdminFormDialog'
import { useAdminRoles, useCreateRole, useUpdateRole, useDeleteRole } from '../hooks/useAdminRoles'
import { toast } from 'react-hot-toast'
import type { RoleRequest } from '../api/adminRoleApi'

export function RoleManagementPage() {
  const { data: roles, isLoading, error, refetch } = useAdminRoles()
  const createMutation = useCreateRole()
  const updateMutation = useUpdateRole()
  const deleteMutation = useDeleteRole()

  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false)
  const [selectedRole, setSelectedRole] = useState<any>(null)

  const [formData, setFormData] = useState<RoleRequest>({ name: '', description: '' })

  const handleOpenModal = (role?: any) => {
    if (role) {
      setSelectedRole(role)
      setFormData({ name: role.name, description: role.description })
    } else {
      setSelectedRole(null)
      setFormData({ name: '', description: '' })
    }
    setIsModalOpen(true)
  }

  const columns = [
    { id: 'name', header: 'Role Name', field: 'name' },
    { id: 'description', header: 'Description', field: 'description' },
    { id: 'permissions', header: 'Permissions', renderCell: (_: any, row: any) => row.permissions?.length || 0 },
    { id: 'createdAt', header: 'Created', field: 'createdAt' },
    {
      id: 'actions', header: 'Actions', align: 'right',
      renderCell: (_: any, row: any) => (
        <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
          <Button variant="ghost" onClick={() => handleOpenModal(row)}>Edit</Button>
          <Button variant="ghost" onClick={() => { setSelectedRole(row); setIsDeleteModalOpen(true) }} style={{ color: '#DC2626' }}>Delete</Button>
        </div>
      )
    }
  ]

  const handleSubmit = async () => {
    try {
      if (selectedRole) {
        await updateMutation.mutateAsync({ uuid: selectedRole.uuid, data: formData })
        toast.success('Role updated')
      } else {
        await createMutation.mutateAsync(formData)
        toast.success('Role created')
      }
      setIsModalOpen(false)
    } catch (err: any) {
      toast.error(err.message)
    }
  }

  return (
    <ContentContainer>
      <PageHeader
        title="Role Management"
        subtitle="Manage system roles and their assigned permissions."
        actions={<Button onClick={() => handleOpenModal()}>Create Role</Button>}
      />

      <Section>
        <AdminTable
          data={roles || []}
          columns={columns}
          rowId="uuid"
          loading={isLoading}
          error={error?.message}
          emptyMessage="No roles found."
          onRefresh={() => refetch()}
        />
      </Section>

      <AdminFormDialog
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={selectedRole ? 'Edit Role' : 'Create Role'}
        isLoading={createMutation.isPending || updateMutation.isPending}
        onSubmit={handleSubmit}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151' }}>Role Name</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData(p => ({ ...p, name: e.target.value }))}
              style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
            />
          </div>
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 500, color: '#374151' }}>Description</label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData(p => ({ ...p, description: e.target.value }))}
              style={{ width: '100%', padding: '0.5rem', borderRadius: '0.375rem', border: '1px solid #D1D5DB' }}
              rows={3}
            />
          </div>
        </div>
      </AdminFormDialog>

      <ConfirmDialog
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="Delete Role"
        description={`Are you sure you want to delete the role ${selectedRole?.name}?`}
        isLoading={deleteMutation.isPending}
        onConfirm={async () => {
          try {
            await deleteMutation.mutateAsync(selectedRole.uuid)
            toast.success('Role deleted')
            setIsDeleteModalOpen(false)
          } catch (err: any) {
            toast.error(err.message)
          }
        }}
      />
    </ContentContainer>
  )
}
