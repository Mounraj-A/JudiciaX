// ─── DocumentViewerPage — Phase F9 ──────────────────────────────────────────
import { useParams, useNavigate } from 'react-router-dom'
import { PageHeader, ContentContainer, Section } from '@/shared/components/layout'
import { Button } from '@/shared/design-system/components/primitives/Button'

export function DocumentViewerPage() {
  const { id } = useParams()
  const navigate = useNavigate()

  return (
    <ContentContainer maxWidth="100%">
      <div className="flex justify-between items-center mb-4">
        <PageHeader 
          title={`Document Viewer: ${id}`}
          description="Judicial Document Analysis Tool"
        />
        <Button variant="outline" onClick={() => navigate(-1)}>
          Go Back
        </Button>
      </div>

      <div className="flex h-[75vh] gap-4">
        {/* Left: Document Viewport */}
        <div className="flex-1 border border-slate-200 dark:border-slate-700 rounded-xl bg-slate-100 dark:bg-slate-950 flex flex-col">
          <div className="h-12 border-b border-slate-200 dark:border-slate-700 flex items-center px-4 justify-between bg-white dark:bg-slate-900 rounded-t-xl">
            <div className="flex gap-2">
              <Button variant="ghost" size="sm">Zoom In</Button>
              <Button variant="ghost" size="sm">Zoom Out</Button>
              <Button variant="ghost" size="sm">Rotate</Button>
            </div>
            <div className="flex gap-2">
              <span className="text-xs text-slate-500">Page 1 of 5</span>
            </div>
          </div>
          <div className="flex-1 flex items-center justify-center p-8 overflow-auto">
            {/* Mock PDF Document */}
            <div className="w-[800px] h-[1000px] bg-white shadow-lg border border-slate-300 p-12 text-black">
              <h1 className="text-2xl font-bold text-center mb-8">IN THE HON'BLE COURT OF JUDICATURE</h1>
              <p className="mb-4">This is a mock representation of the official document for ID: {id}.</p>
              <p className="mb-4">The actual implementation would embed a full PDF viewer component here.</p>
              <div className="border-t border-black mt-12 pt-4">
                <p>Signature</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Annotations & Metadata */}
        <div className="w-80 flex flex-col gap-4">
          <Section title="Metadata" bordered padded className="flex-1 overflow-auto">
            <div className="text-sm space-y-2">
              <p><strong>Title:</strong> Affadavit</p>
              <p><strong>Submitted By:</strong> Alice Advocate</p>
              <p><strong>Date:</strong> 2026-06-16</p>
            </div>
          </Section>
          <Section title="Annotations" bordered padded className="flex-1 overflow-auto">
            <p className="text-xs text-slate-500 italic">No annotations added yet.</p>
            <Button variant="outline" size="sm" className="w-full mt-4">Add Annotation</Button>
          </Section>
        </div>
      </div>
    </ContentContainer>
  )
}
