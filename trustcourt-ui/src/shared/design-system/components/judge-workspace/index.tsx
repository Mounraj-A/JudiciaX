import React from 'react'

// Judge Workspace Components — placeholder shells for future business logic

// ─── Case Header ──────────────────────────────────────────────────────────────
interface CaseHeaderProps {
  caseId: string
  caseTitle: string
  caseType?: string
  filingDate?: string
  status?: string
  actions?: React.ReactNode
}

function CaseHeader({ caseId, caseTitle, caseType, filingDate, status, actions }: CaseHeaderProps) {
  return (
    <div className="bg-white border border-[#E5E7EB] rounded-xl p-6 shadow-soft">
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2 text-xs text-[#9CA3AF]">
            <span className="font-mono font-medium text-[#374151]">{caseId}</span>
            {caseType && <><span>·</span><span>{caseType}</span></>}
            {filingDate && <><span>·</span><time>Filed {filingDate}</time></>}
          </div>
          <h1 className="text-xl font-semibold text-[#111827] text-balance">{caseTitle}</h1>
        </div>
        <div className="flex items-center gap-3 shrink-0">
          {status && (
            <span className="text-xs font-medium bg-[#F0FDF4] text-[#059669] border border-[#BBF7D0] px-3 py-1 rounded-full">
              {status}
            </span>
          )}
          {actions}
        </div>
      </div>
    </div>
  )
}

// ─── Evidence Panel ───────────────────────────────────────────────────────────
interface EvidencePanelProps {
  items?: { id: string; label: string; type: string; status: string }[]
  onSelect?: (id: string) => void
}

function EvidencePanel({ items = [], onSelect }: EvidencePanelProps) {
  return (
    <aside className="bg-white border border-[#E5E7EB] rounded-xl p-4 shadow-soft" aria-label="Evidence Panel">
      <h2 className="text-sm font-semibold text-[#111827] mb-3">Evidence ({items.length})</h2>
      {items.length === 0 ? (
        <p className="text-xs text-[#9CA3AF] py-4 text-center">No evidence items</p>
      ) : (
        <ul className="space-y-2">
          {items.map((item) => (
            <li key={item.id}>
              <button
                onClick={() => onSelect?.(item.id)}
                className="w-full text-left px-3 py-2 rounded-lg hover:bg-[#F9FAFB] transition-colors"
              >
                <p className="text-sm font-medium text-[#374151]">{item.label}</p>
                <p className="text-xs text-[#9CA3AF]">{item.type} · {item.status}</p>
              </button>
            </li>
          ))}
        </ul>
      )}
    </aside>
  )
}

// ─── Notes Panel ──────────────────────────────────────────────────────────────
function NotesPanel({ placeholder = 'Add notes...' }: { placeholder?: string }) {
  const [notes, setNotes] = React.useState('')
  return (
    <div className="bg-white border border-[#E5E7EB] rounded-xl p-4 shadow-soft" aria-label="Notes Panel">
      <h2 className="text-sm font-semibold text-[#111827] mb-3">Notes</h2>
      <textarea
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
        placeholder={placeholder}
        rows={6}
        className="w-full text-sm text-[#374151] border border-[#E5E7EB] rounded-lg p-3 resize-none focus:outline-none focus:ring-2 focus:ring-[#1E3A8A] focus:border-transparent"
        aria-label="Case notes"
      />
      <p className="text-xs text-[#9CA3AF] mt-1">{notes.length} characters</p>
    </div>
  )
}

// ─── Document Viewer Placeholder ──────────────────────────────────────────────
function DocumentViewerPlaceholder({ documentName }: { documentName?: string }) {
  return (
    <div
      className="flex flex-col items-center justify-center min-h-[400px] bg-[#F8F9FA] border-2 border-dashed border-[#E5E7EB] rounded-xl"
      aria-label="Document viewer area"
    >
      <div className="text-center space-y-2">
        <div className="h-16 w-16 rounded-2xl bg-white border border-[#E5E7EB] shadow-soft flex items-center justify-center mx-auto text-[#9CA3AF] text-2xl">
          📄
        </div>
        <p className="text-sm font-medium text-[#374151]">
          {documentName ?? 'Document Viewer'}
        </p>
        <p className="text-xs text-[#9CA3AF]">Phase FX – PDF integration pending</p>
      </div>
    </div>
  )
}

// ─── Recommendation Panel ─────────────────────────────────────────────────────
interface RecommendationPanelProps {
  recommendations?: { id: string; title: string; priority: string; description: string }[]
}

const priorityColors: Record<string, string> = {
  CRITICAL: '#B91C1C',
  HIGH:     '#D97706',
  MEDIUM:   '#0369A1',
  LOW:      '#059669',
}

function RecommendationPanel({ recommendations = [] }: RecommendationPanelProps) {
  return (
    <div className="bg-white border border-[#E5E7EB] rounded-xl p-4 shadow-soft" aria-label="AI Recommendations">
      <h2 className="text-sm font-semibold text-[#111827] mb-3">
        AI Recommendations ({recommendations.length})
      </h2>
      {recommendations.length === 0 ? (
        <p className="text-xs text-[#9CA3AF] py-4 text-center">No recommendations available</p>
      ) : (
        <ul className="space-y-3">
          {recommendations.map((rec) => (
            <li
              key={rec.id}
              className="rounded-lg p-3 border-l-4 bg-[#F9FAFB]"
              style={{ borderLeftColor: priorityColors[rec.priority] ?? '#9CA3AF' }}
            >
              <p className="text-sm font-medium text-[#374151]">{rec.title}</p>
              <p className="text-xs text-[#6B7280] mt-0.5">{rec.description}</p>
              <span
                className="text-[10px] font-semibold uppercase tracking-wide mt-1 inline-block"
                style={{ color: priorityColors[rec.priority] ?? '#6B7280' }}
              >
                {rec.priority}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

// ─── Decision Panel ───────────────────────────────────────────────────────────
function DecisionPanel() {
  return (
    <div className="bg-white border-2 border-[#0F1D3A] rounded-xl p-4 shadow-soft" aria-label="Decision Panel">
      <h2 className="text-sm font-semibold text-[#0F1D3A] mb-1">Decision Panel</h2>
      <p className="text-xs text-[#6B7280] mb-4">
        This panel is reserved for judge decision recording. Business logic implemented in Phase FX.
      </p>
      <div className="bg-[#F8F9FA] rounded-lg p-4 text-center border border-dashed border-[#E5E7EB]">
        <p className="text-xs text-[#9CA3AF]">Decision workflows — Phase FX</p>
      </div>
    </div>
  )
}

export {
  CaseHeader,
  EvidencePanel,
  NotesPanel,
  DocumentViewerPlaceholder,
  RecommendationPanel,
  DecisionPanel,
}
