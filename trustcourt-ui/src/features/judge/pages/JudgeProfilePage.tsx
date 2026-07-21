// ─── JudgeProfilePage — Phase F9 ────────────────────────────────────────────
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Input } from '@/shared/components/form'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { useAuthContext } from '@/features/auth/contexts/AuthContext'

export function JudgeProfilePage() {
  const { logout } = useAuthContext()
  return (
    <ContentContainer maxWidth="800px">
      <PageHeader title="Judge Profile & Settings" description="Manage your preferences and security settings." />
      
      <Section title="Profile Information" bordered padded>
        <div className="space-y-4">
          <Input label="Full Name" value="Hon. Sarah Justice" readOnly />
          <Input label="Email" value="judge.sarah@trustcourt.gov.in" readOnly />
          <Input label="Assigned Court" value="High Court - Commercial Division" readOnly />
        </div>
      </Section>
      
      <Section title="Workspace Preferences" bordered padded>
        <div className="space-y-4">
          <div className="flex items-center justify-between p-3 border rounded-lg">
            <div>
              <p className="font-medium text-sm">Default View</p>
              <p className="text-xs text-slate-500">Choose landing page after login</p>
            </div>
            <select className="border p-2 rounded-md text-sm bg-white dark:bg-slate-800">
              <option>Judge Dashboard</option>
              <option>Cause List</option>
            </select>
          </div>
        </div>
      </Section>

      <div className="flex justify-between items-center mt-6">
        <Button variant="outline" className="text-red-600 border-red-200 hover:bg-red-50 hover:text-red-700" onClick={() => logout().catch(() => {})}>Sign Out</Button>
        <div className="flex gap-3">
          <Button variant="outline">Cancel</Button>
          <Button variant="primary">Save Changes</Button>
        </div>
      </div>
    </ContentContainer>
  )
}
