// ─── DocumentAdminPage — Phase F10 ───────────────────────────────────────────
import { PageHeader, ContentContainer, Section, GridLayout, CardSection } from '@/shared/components/layout'
import { StatusBadge } from '@/shared/components/badges'
import { Button } from '@/shared/design-system/components/primitives/Button'

export function DocumentAdminPage() {
  return (
    <ContentContainer>
      <PageHeader
        title="Document Administration"
        subtitle="Platform-wide document repository, storage metrics, and verification status."
        actions={<Button>Manage Storage</Button>}
      />

      <GridLayout cols={4}>
        <CardSection title="Total Documents" className="text-center">
          <div className="text-3xl font-bold text-[#111827]">1.2M</div>
        </CardSection>
        <CardSection title="Storage Used" className="text-center">
          <div className="text-3xl font-bold text-[#1E40AF]">18.4 TB</div>
        </CardSection>
        <CardSection title="Verified Documents" className="text-center">
          <div className="text-3xl font-bold text-[#065F46]">85%</div>
        </CardSection>
        <CardSection title="Pending Verification" className="text-center">
          <div className="text-3xl font-bold text-[#D97706]">15%</div>
        </CardSection>
      </GridLayout>

      <GridLayout cols={2} className="mt-6">
        <Section title="Storage Distribution">
          <div className="space-y-4 pt-2">
            <div>
              <div className="flex justify-between text-sm mb-1"><span>Evidence & Annexures</span><span>12.5 TB</span></div>
              <div className="w-full bg-[#E5E7EB] rounded-full h-2"><div className="bg-[#1E40AF] h-2 rounded-full" style={{ width: '68%' }}></div></div>
            </div>
            <div>
              <div className="flex justify-between text-sm mb-1"><span>Judgments & Orders</span><span>4.2 TB</span></div>
              <div className="w-full bg-[#E5E7EB] rounded-full h-2"><div className="bg-[#059669] h-2 rounded-full" style={{ width: '22%' }}></div></div>
            </div>
            <div>
              <div className="flex justify-between text-sm mb-1"><span>Pleadings & Petitions</span><span>1.7 TB</span></div>
              <div className="w-full bg-[#E5E7EB] rounded-full h-2"><div className="bg-[#D97706] h-2 rounded-full" style={{ width: '10%' }}></div></div>
            </div>
          </div>
        </Section>
        
        <Section title="Recent Document Anomalies">
          <div className="space-y-3">
            <div className="p-3 border border-[#E5E7EB] rounded-md flex justify-between items-center">
              <div>
                <div className="font-semibold text-sm text-[#111827]">DOC-2023-4419</div>
                <div className="text-xs text-[#6B7280]">Digital signature mismatch detected by OCR pipeline.</div>
              </div>
              <StatusBadge status="failed" label="Flagged" />
            </div>
            <div className="p-3 border border-[#E5E7EB] rounded-md flex justify-between items-center">
              <div>
                <div className="font-semibold text-sm text-[#111827]">DOC-2023-4412</div>
                <div className="text-xs text-[#6B7280]">Malware scan timeout during upload.</div>
              </div>
              <StatusBadge status="pending" label="Quarantined" />
            </div>
            <div className="p-3 border border-[#E5E7EB] rounded-md flex justify-between items-center">
              <div>
                <div className="font-semibold text-sm text-[#111827]">DOC-2023-4398</div>
                <div className="text-xs text-[#6B7280]">File size exceeds 500MB policy limit.</div>
              </div>
              <StatusBadge status="inactive" label="Rejected" />
            </div>
          </div>
        </Section>
      </GridLayout>
    </ContentContainer>
  )
}
