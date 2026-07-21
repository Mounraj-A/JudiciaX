// ─── Export Types — Phase F3 ──────────────────────────────────────────────────

export type ExportFormat = 'csv' | 'excel' | 'pdf' | 'print' | 'json'

export interface ExportColumn {
  id:       string
  header:   string
  field?:   string
  format?:  (value: unknown) => string | number
}

export interface ExportConfig {
  filename:     string
  format:       ExportFormat
  columns:      ExportColumn[]
  title?:       string
  subtitle?:    string
  author?:      string
  timestamp?:   boolean
  includeFilters?: boolean
}

export interface ExportState {
  isExporting: boolean
  progress?:   number
  error?:      string | null
  lastExport?: { format: ExportFormat; filename: string; exportedAt: string }
}

export type ExportHandler<T = unknown> = (
  data:   T[],
  config: ExportConfig,
) => Promise<void>
