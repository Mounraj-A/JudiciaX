// ─── Export Utilities — Phase F3 ──────────────────────────────────────────────
import { buildCSV } from '../table'
import type { ColumnDef } from '@/types/shared/table'
import type { ExportConfig } from '@/types/shared/export'

/** Trigger browser CSV download */
export function exportCSV<T extends Record<string, unknown>>(
  data:     T[],
  columns:  ColumnDef<T>[],
  filename: string,
): void {
  const csv  = buildCSV(data, columns)
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' }) // BOM for Excel
  downloadBlob(blob, `${filename}.csv`)
}

/** Trigger browser JSON download */
export function exportJSON<T>(data: T[], filename: string): void {
  const json = JSON.stringify(data, null, 2)
  const blob = new Blob([json], { type: 'application/json' })
  downloadBlob(blob, `${filename}.json`)
}

/** Trigger print dialog */
export function exportPrint(title?: string): void {
  const prev    = document.title
  if (title) document.title = title
  window.print()
  if (title) document.title = prev
}

/** Future: Excel export stub — connect SheetJS when needed */
export async function exportExcel<T extends Record<string, unknown>>(
  _data:     T[],
  _columns:  ColumnDef<T>[],
  _filename: string,
): Promise<void> {
  console.warn('[ExportUtils] Excel export requires SheetJS. Install xlsx and implement here.')
}

/** Future: PDF export stub — connect pdfmake/jsPDF when needed */
export async function exportPDF(
  _config: ExportConfig,
): Promise<void> {
  console.warn('[ExportUtils] PDF export requires a PDF library. Install jsPDF and implement here.')
}

// ─── Internal ────────────────────────────────────────────────────────────────
function downloadBlob(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob)
  const a   = Object.assign(document.createElement('a'), { href: url, download: filename })
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
