import React, { useState } from 'react'
import { ThemeProvider, useTheme, type ThemeName, type DensityMode } from '@/shared/design-system/theme/ThemeProvider'
import { Button } from '@/shared/design-system/components/primitives/Button'
import { Badge } from '@/shared/design-system/components/primitives/Badge'
import { Input } from '@/shared/design-system/components/primitives/Input'
import { Skeleton, Progress, LoadingSpinner, EmptyState, ErrorState, Alert } from '@/shared/design-system/components/feedback'
import { Dialog, Drawer } from '@/shared/design-system/components/overlay'
import { Tabs, Breadcrumb, Pagination } from '@/shared/design-system/components/navigation'
import { PriorityScoreCard, TrustScoreCard, ConfidenceMeter, RiskIndicator, PipelineStatus } from '@/shared/design-system/components/ai'
import { EnterpriseTable, StatusTimeline } from '@/shared/design-system/components/data-display'
import { CaseHeader, EvidencePanel, NotesPanel, DocumentViewerPlaceholder, RecommendationPanel, DecisionPanel } from '@/shared/design-system/components/judge-workspace'
import { Grid } from '@/shared/design-system/layouts'
import { Display, Heading, Title, Subtitle, Body, Caption, Label, Code } from '@/shared/design-system/typography'
import { colors } from '@/shared/design-system/tokens/colors'

// ─── Section wrapper ──────────────────────────────────────────────────────────
function ShowSection({ title, id, children }: { title: string; id: string; children: React.ReactNode }) {
  return (
    <section id={id} className="space-y-4 scroll-mt-20">
      <div className="flex items-center gap-3">
        <h2 className="text-lg font-semibold text-[#111827]">{title}</h2>
        <div className="flex-1 h-px bg-[#E5E7EB]" />
      </div>
      {children}
    </section>
  )
}

// ─── Theme Switcher ───────────────────────────────────────────────────────────
function ThemeSwitcher() {
  const { theme, setTheme, density, setDensity } = useTheme()
  const themes: ThemeName[] = ['light', 'dark', 'high-contrast', 'court', 'government', 'research']
  const densities: DensityMode[] = ['compact', 'default', 'comfortable']

  return (
    <div className="flex flex-wrap items-center gap-3 p-4 bg-[#F8F9FA] rounded-xl border border-[#E5E7EB]">
      <div className="flex items-center gap-2">
        <Label>Theme:</Label>
        <div className="flex gap-1">
          {themes.map((t) => (
            <button
              key={t}
              onClick={() => setTheme(t)}
              className={`px-2.5 py-1 rounded text-xs font-medium transition-all ${theme === t ? 'bg-[#0F1D3A] text-white' : 'bg-white text-[#374151] border border-[#E5E7EB] hover:bg-[#F3F4F6]'}`}
            >
              {t}
            </button>
          ))}
        </div>
      </div>
      <div className="flex items-center gap-2">
        <Label>Density:</Label>
        <div className="flex gap-1">
          {densities.map((d) => (
            <button
              key={d}
              onClick={() => setDensity(d)}
              className={`px-2.5 py-1 rounded text-xs font-medium transition-all ${density === d ? 'bg-[#C2410C] text-white' : 'bg-white text-[#374151] border border-[#E5E7EB] hover:bg-[#F3F4F6]'}`}
            >
              {d}
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}

// ─── Showcase inner ───────────────────────────────────────────────────────────
function ShowcaseInner() {
  const [dialogOpen, setDialogOpen] = useState(false)
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [activeTab, setActiveTab] = useState('overview')
  const [page, setPage] = useState(1)

  const mockPipelineStages = [
    { id: 'ocr',      label: 'OCR Processing',  status: 'completed' as const, module: 'ocr'      as const, duration: '245ms' },
    { id: 'nlp',      label: 'NLP Extraction',  status: 'completed' as const, module: 'nlp'      as const, duration: '180ms' },
    { id: 'feature',  label: 'Feature Eng.',    status: 'completed' as const, module: 'feature'  as const, duration: '52ms'  },
    { id: 'jpi',      label: 'JPI Calculation', status: 'processing'as const, module: 'jpi'      as const },
    { id: 'cts',      label: 'Trust Score',     status: 'idle'      as const, module: 'cts'      as const },
    { id: 'xai',      label: 'Explainability',  status: 'idle'      as const, module: 'xai'      as const },
    { id: 'decision', label: 'Decision Support',status: 'idle'      as const, module: 'decision' as const },
  ]

  const mockTableData = [
    { id: '1', caseNo: 'TC-2024-001', petitioner: 'Rajesh Kumar', type: 'Civil', jpi: 87.5, cts: 72.1, status: 'Under Review', date: '2024-01-15' },
    { id: '2', caseNo: 'TC-2024-002', petitioner: 'Priya Sharma', type: 'Criminal', jpi: 95.2, cts: 45.8, status: 'Emergency',   date: '2024-01-16' },
    { id: '3', caseNo: 'TC-2024-003', petitioner: 'Arjun Nair',   type: 'Civil', jpi: 55.0, cts: 88.3, status: 'Pending',     date: '2024-01-17' },
  ]

  const tableColumns = [
    { key: 'caseNo',     header: 'Case No',    accessor: (r: typeof mockTableData[0]) => <Code>{r.caseNo}</Code>, sortable: true },
    { key: 'petitioner', header: 'Petitioner', accessor: (r: typeof mockTableData[0]) => r.petitioner },
    { key: 'type',       header: 'Type',       accessor: (r: typeof mockTableData[0]) => r.type },
    { key: 'jpi',        header: 'JPI',        accessor: (r: typeof mockTableData[0]) => <span className="font-mono font-semibold text-[#EA580C]">{r.jpi}</span>, sortable: true },
    { key: 'cts',        header: 'CTS',        accessor: (r: typeof mockTableData[0]) => <span className="font-mono font-semibold text-[#0F766E]">{r.cts}</span>, sortable: true },
    { key: 'status',     header: 'Status',     accessor: (r: typeof mockTableData[0]) => <Badge variant={r.status === 'Emergency' ? 'emergency' : r.status === 'Pending' ? 'medium' : 'info'} dot>{r.status}</Badge> },
  ]

  return (
    <div className="min-h-screen bg-[#F8F9FA]">
      {/* Fixed header */}
      <header className="sticky top-0 z-50 bg-[#0F1D3A] border-b border-white/10 px-8 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="h-8 w-8 rounded-lg bg-[#C2410C] flex items-center justify-center text-white font-bold text-sm">TC</div>
          <div>
            <h1 className="text-sm font-semibold text-white">TrustCourt Design System</h1>
            <p className="text-xs text-white/50">Phase F0 – Enterprise Foundation v1.0.0</p>
          </div>
        </div>
        <a href="#top" className="text-xs text-white/50 hover:text-white/80 transition-colors">↑ Top</a>
      </header>

      <div className="max-w-7xl mx-auto px-8 py-10 space-y-14">
        {/* Hero */}
        <div className="text-center space-y-4 py-8">
          <Display>Enterprise Judicial Design System</Display>
          <Subtitle muted>TrustCourt · Phase F0 · 50+ reusable components</Subtitle>
          <ThemeSwitcher />
        </div>

        {/* Color Palette */}
        <ShowSection title="Color System" id="colors">
          <div className="space-y-6">
            <div>
              <Label className="mb-3 block">Judicial Palette</Label>
              <div className="grid grid-cols-5 gap-3">
                {[
                  { name: 'Primary (Deep Navy)', color: '#0F1D3A' },
                  { name: 'Secondary (Royal Blue)', color: '#1E3A8A' },
                  { name: 'Accent (Judicial Orange)', color: '#C2410C' },
                  { name: 'Success (Emerald)', color: '#059669' },
                  { name: 'Warning (Amber)', color: '#D97706' },
                  { name: 'Danger (Crimson)', color: '#B91C1C' },
                  { name: 'Info (Sky Blue)', color: '#0369A1' },
                  { name: 'Surface', color: '#F8F9FA' },
                  { name: 'Border', color: '#E5E7EB' },
                  { name: 'Text Primary', color: '#111827' },
                ].map((c) => (
                  <div key={c.name} className="space-y-1.5">
                    <div className="h-12 rounded-lg border border-[#E5E7EB]" style={{ backgroundColor: c.color }} />
                    <p className="text-[10px] text-[#6B7280] font-medium leading-tight">{c.name}</p>
                    <Code>{c.color}</Code>
                  </div>
                ))}
              </div>
            </div>
            <div>
              <Label className="mb-3 block">AI Module Palette</Label>
              <div className="grid grid-cols-8 gap-3">
                {Object.entries(colors.ai).map(([key, val]) => (
                  <div key={key} className="space-y-1.5">
                    <div className="h-10 rounded-lg" style={{ backgroundColor: (val as { DEFAULT: string }).DEFAULT }} />
                    <p className="text-[10px] text-[#6B7280] font-medium uppercase">{key}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </ShowSection>

        {/* Typography */}
        <ShowSection title="Typography" id="typography">
          <div className="bg-white rounded-xl border border-[#E5E7EB] p-8 space-y-6">
            <Display>Display — Judicial Decision Support</Display>
            <Heading>Heading — Case Management Platform</Heading>
            <Title>Title — Active Cases (2,847)</Title>
            <Subtitle>Subtitle — High Priority Cases Requiring Attention</Subtitle>
            <Body>Body — This is the standard body text used across the TrustCourt application for legal descriptions, case details, and general information. Line height is set to 1.625 for optimal readability in legal contexts.</Body>
            <Caption>Caption — Filed on January 15, 2024 · Case ID: TC-2024-001</Caption>
            <Label>Label — Priority Index</Label>
            <Code>TC-2024-001 · JPI: 87.5 · CTS: 72.1</Code>
          </div>
        </ShowSection>

        {/* Buttons */}
        <ShowSection title="Button System" id="buttons">
          <div className="bg-white rounded-xl border border-[#E5E7EB] p-6 space-y-4">
            <div className="flex flex-wrap gap-3 items-center">
              <Button variant="primary">Primary Action</Button>
              <Button variant="secondary">Secondary</Button>
              <Button variant="accent">Accent</Button>
              <Button variant="ghost">Ghost</Button>
              <Button variant="destructive">Destructive</Button>
              <Button variant="outline">Outline</Button>
              <Button variant="link">Link</Button>
            </div>
            <div className="flex flex-wrap gap-3 items-center">
              <Button size="sm">Small</Button>
              <Button size="md">Medium</Button>
              <Button size="lg">Large</Button>
              <Button variant="primary" isLoading>Loading...</Button>
              <Button variant="primary" disabled>Disabled</Button>
            </div>
          </div>
        </ShowSection>

        {/* Badges */}
        <ShowSection title="Badge & Status Chips" id="badges">
          <div className="bg-white rounded-xl border border-[#E5E7EB] p-6">
            <div className="flex flex-wrap gap-2">
              {(['default','secondary','success','warning','danger','info','emergency','critical','high','medium','low','ai-jpi','ai-cts','ai-xai','ai-governance'] as const).map((v) => (
                <Badge key={v} variant={v} dot>{v}</Badge>
              ))}
            </div>
          </div>
        </ShowSection>

        {/* Forms */}
        <ShowSection title="Form Controls" id="forms">
          <div className="bg-white rounded-xl border border-[#E5E7EB] p-6">
            <Grid cols={3} gap={6}>
              <Input label="Case ID" placeholder="TC-2024-001" />
              <Input label="Petitioner Name" placeholder="Full name" helperText="Enter legal name as per ID" />
              <Input label="Filing Date" type="date" error="Date is required" />
            </Grid>
          </div>
        </ShowSection>

        {/* Feedback */}
        <ShowSection title="Feedback Components" id="feedback">
          <div className="space-y-4">
            <Alert variant="info" title="Information">Case TC-2024-001 has been assigned to Judge Arjun Nair.</Alert>
            <Alert variant="success" title="Success">Decision support report generated successfully.</Alert>
            <Alert variant="warning" title="Warning">JPI score exceeds emergency threshold. Immediate review required.</Alert>
            <Alert variant="danger" title="Critical Risk" onDismiss={() => {}}>Trust score is critically low (42.1). Additional verification needed.</Alert>
            <Grid cols={3} gap={4}>
              <div className="space-y-3 p-4 bg-white rounded-xl border border-[#E5E7EB]">
                <Label>Skeleton Loading</Label>
                <Skeleton height={20} className="w-3/4" />
                <Skeleton height={16} className="w-1/2" />
                <Skeleton height={60} />
              </div>
              <div className="space-y-3 p-4 bg-white rounded-xl border border-[#E5E7EB]">
                <Label>Progress Bars</Label>
                <Progress value={87.5} max={100} label="JPI Score" showValue color="ai-jpi" />
                <Progress value={72.1} max={100} label="CTS Score" showValue color="ai-cts" />
                <Progress value={45} max={100} label="Readiness" showValue color="warning" />
              </div>
              <div className="flex flex-col items-center justify-center gap-4 p-4 bg-white rounded-xl border border-[#E5E7EB]">
                <Label>Spinners</Label>
                <div className="flex gap-4">
                  <LoadingSpinner size="sm" />
                  <LoadingSpinner size="md" />
                  <LoadingSpinner size="lg" color="#EA580C" />
                </div>
              </div>
            </Grid>
          </div>
        </ShowSection>

        {/* Overlays */}
        <ShowSection title="Overlays" id="overlays">
          <div className="flex gap-4">
            <Button variant="secondary" onClick={() => setDialogOpen(true)}>Open Dialog</Button>
            <Button variant="secondary" onClick={() => setDrawerOpen(true)}>Open Drawer</Button>
          </div>
          <Dialog
            open={dialogOpen}
            onClose={() => setDialogOpen(false)}
            title="Confirm Action"
            description="This action will submit the case for judicial review. Please confirm."
            footer={
              <>
                <Button variant="ghost" onClick={() => setDialogOpen(false)}>Cancel</Button>
                <Button variant="primary" onClick={() => setDialogOpen(false)}>Confirm</Button>
              </>
            }
          >
            <Body>Are you sure you want to submit Case TC-2024-001 for review?</Body>
          </Dialog>
          <Drawer open={drawerOpen} onClose={() => setDrawerOpen(false)} title="Case Details">
            <div className="space-y-4">
              <Body>Drawer content for detailed case information.</Body>
              <Progress value={75} max={100} label="Case Completeness" showValue color="primary" />
            </div>
          </Drawer>
        </ShowSection>

        {/* Navigation */}
        <ShowSection title="Navigation" id="navigation">
          <div className="space-y-4">
            <Breadcrumb items={[{ label: 'Home', href: '#' }, { label: 'Cases', href: '#' }, { label: 'TC-2024-001' }]} />
            <Tabs
              tabs={[
                { id: 'overview',   label: 'Overview'   },
                { id: 'documents',  label: 'Documents', badge: 12 },
                { id: 'timeline',   label: 'Timeline'  },
                { id: 'ai',         label: 'AI Reports' },
                { id: 'decision',   label: 'Decision',  disabled: true },
              ]}
              activeTab={activeTab}
              onChange={setActiveTab}
            />
            <div className="flex items-center justify-center">
              <Pagination page={page} total={240} pageSize={20} onChange={setPage} />
            </div>
          </div>
        </ShowSection>

        {/* Enterprise Table */}
        <ShowSection title="Enterprise Table" id="table">
          <EnterpriseTable
            columns={tableColumns}
            data={mockTableData}
            selectable
            stickyHeader
            caption="Active Cases"
            pagination={{ page: 1, total: 84, pageSize: 10, onChange: () => {} }}
          />
        </ShowSection>

        {/* AI Components */}
        <ShowSection title="AI Components" id="ai">
          <Grid cols={3} gap={6}>
            <PriorityScoreCard jpiScore={95.2} caseId="TC-2024-002" caseType="Criminal" filingDate="Jan 16" />
            <PriorityScoreCard jpiScore={72.8} caseId="TC-2024-005" caseType="Civil"    filingDate="Jan 14" />
            <PriorityScoreCard jpiScore={38.4} caseId="TC-2024-007" caseType="Revenue"  filingDate="Jan 12" />
          </Grid>
          <Grid cols={2} gap={6}>
            <TrustScoreCard
              ctsScore={72.1}
              caseId="TC-2024-002"
              factors={[
                { label: 'Document Integrity', score: 85 },
                { label: 'OCR Confidence',     score: 92 },
                { label: 'Consistency Score',  score: 68 },
              ]}
            />
            <div className="bg-white border border-[#E5E7EB] rounded-xl p-5 space-y-4 shadow-soft">
              <Label>Confidence Meters</Label>
              <ConfidenceMeter value={88} module="xai" label="XAI Explanation Confidence" />
              <ConfidenceMeter value={65} module="jpi" label="JPI Confidence" />
              <ConfidenceMeter value={42} module="cts" label="CTS Confidence" />
            </div>
          </Grid>
          <Grid cols={2} gap={6}>
            <div className="bg-white border border-[#E5E7EB] rounded-xl p-5 shadow-soft">
              <Label className="mb-4 block">Risk Indicators</Label>
              <div className="space-y-3">
                <RiskIndicator severity="critical" title="Low Trust Score" description="CTS below 50 — additional verification required" />
                <RiskIndicator severity="high"     title="Missing Required Documents" description="3 mandatory documents not uploaded" />
                <RiskIndicator severity="medium"   title="Deadline Approaching" description="Statutory deadline in 7 days" />
                <RiskIndicator severity="low"      title="Minor Discrepancy" description="Name spelling inconsistency detected" />
              </div>
            </div>
            <div className="bg-white border border-[#E5E7EB] rounded-xl p-5 shadow-soft">
              <Label className="mb-4 block">Pipeline Status</Label>
              <PipelineStatus stages={mockPipelineStages} />
            </div>
          </Grid>
        </ShowSection>

        {/* Status Timeline */}
        <ShowSection title="Status Timeline" id="timeline">
          <div className="bg-white border border-[#E5E7EB] rounded-xl p-6 shadow-soft max-w-lg">
            <StatusTimeline events={[
              { id: '1', label: 'Case Filed',       timestamp: 'Jan 15, 2024 · 09:15',  status: 'completed', actor: 'Advocate R. Kumar',  description: 'Initial petition submitted' },
              { id: '2', label: 'Documents Uploaded',timestamp: 'Jan 15, 2024 · 10:32', status: 'completed', actor: 'Clerk M. Priya' },
              { id: '3', label: 'AI Processing',    timestamp: 'Jan 15, 2024 · 10:33',  status: 'completed', description: 'JPI: 87.5 · CTS: 72.1' },
              { id: '4', label: 'Assigned to Judge', timestamp: 'Jan 16, 2024 · 09:00', status: 'current',   actor: 'Judge A. Nair' },
              { id: '5', label: 'First Hearing',    timestamp: 'Jan 23, 2024 · 10:00',  status: 'upcoming' },
            ]} />
          </div>
        </ShowSection>

        {/* Judge Workspace */}
        <ShowSection title="Judge Workspace Components" id="workspace">
          <CaseHeader
            caseId="TC-2024-002"
            caseTitle="Kumar vs. State of Tamil Nadu — Land Acquisition Dispute"
            caseType="Civil"
            filingDate="Jan 16, 2024"
            status="Under Review"
            actions={<Button variant="secondary" size="sm">Schedule Hearing</Button>}
          />
          <Grid cols={3} gap={6}>
            <EvidencePanel items={[
              { id: 'e1', label: 'Land Records (Survey No. 124)', type: 'PDF', status: 'Verified'  },
              { id: 'e2', label: 'Government Notification', type: 'PDF', status: 'Verified'  },
              { id: 'e3', label: 'Compensation Assessment', type: 'PDF', status: 'Pending'   },
            ]} />
            <NotesPanel placeholder="Add case notes here..." />
            <RecommendationPanel recommendations={[
              { id: 'r1', title: 'Request Land Survey', priority: 'HIGH',   description: 'Conflicting boundaries detected in submitted documents.' },
              { id: 'r2', title: 'Mark Ready for Hearing', priority: 'MEDIUM', description: 'All core documents verified.' },
            ]} />
          </Grid>
          <Grid cols={2} gap={6}>
            <DocumentViewerPlaceholder documentName="Land Records — Survey No. 124.pdf" />
            <DecisionPanel />
          </Grid>
        </ShowSection>

        {/* Empty/Error States */}
        <ShowSection title="States" id="states">
          <Grid cols={2} gap={6}>
            <div className="bg-white border border-[#E5E7EB] rounded-xl overflow-hidden shadow-soft">
              <EmptyState
                title="No Cases Found"
                description="There are no cases matching your current filters. Try adjusting your search criteria."
                icon={<span className="text-3xl">⚖️</span>}
                action={<Button variant="secondary" size="sm">Clear Filters</Button>}
              />
            </div>
            <div className="bg-white border border-[#E5E7EB] rounded-xl overflow-hidden shadow-soft">
              <ErrorState
                title="Failed to Load Cases"
                description="Unable to connect to the backend. Please check your connection."
                retry={() => alert('Retrying...')}
              />
            </div>
          </Grid>
        </ShowSection>

        {/* Footer */}
        <footer className="border-t border-[#E5E7EB] pt-8 pb-12 text-center space-y-2">
          <Caption>TrustCourt Design System · Phase F0 · Enterprise Judicial UI Foundation</Caption>
          <Caption>Built with React 19 · TypeScript · TailwindCSS · Framer Motion · Shadcn UI</Caption>
          <Caption muted={false}>© 2024 TrustCourt. All rights reserved.</Caption>
        </footer>
      </div>
    </div>
  )
}

// ─── Showcase Page (wrapped with providers) ───────────────────────────────────
export default function ShowcasePage() {
  return (
    <ThemeProvider>
      <ShowcaseInner />
    </ThemeProvider>
  )
}
