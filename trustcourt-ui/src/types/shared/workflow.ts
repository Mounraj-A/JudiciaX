// ─── Workflow Types — Phase F3 ────────────────────────────────────────────────

export type StageStatus = 'pending' | 'active' | 'completed' | 'failed' | 'skipped' | 'waiting'

export interface WorkflowStage {
  id:          string
  label:       string
  description?:string
  status:      StageStatus
  completedAt?:string   // ISO
  actor?:      { id: string; name: string }
  notes?:      string
  order:       number
}

export interface WorkflowDefinition {
  id:       string
  name:     string
  stages:   WorkflowStage[]
  currentStageId: string | null
}

export type ApprovalAction = 'approve' | 'reject' | 'request_revision' | 'escalate' | 'withdraw'

export interface ApprovalStep {
  id:         string
  approver:   { id: string; name: string; role: string }
  action?:    ApprovalAction
  comment?:   string
  decidedAt?: string   // ISO
  order:      number
  required:   boolean
}

export interface ApprovalFlow {
  id:        string
  title:     string
  steps:     ApprovalStep[]
  status:    'pending' | 'approved' | 'rejected' | 'withdrawn'
  createdAt: string
  updatedAt: string
}

export interface HistoryEntry {
  id:        string
  event:     string
  detail?:   string
  actor?:    { id: string; name: string }
  timestamp: string  // ISO
  icon?:     string
  color?:    string
}

export interface DecisionOption {
  id:       string
  label:    string
  value:    string
  variant?: 'primary' | 'success' | 'danger' | 'warning' | 'secondary'
  icon?:    string
  confirm?: boolean  // requires confirmation dialog
}
