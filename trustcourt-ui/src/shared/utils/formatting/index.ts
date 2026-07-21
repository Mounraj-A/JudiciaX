// ─── Formatting Utilities — Phase F3 ─────────────────────────────────────────

/** Format a date string or timestamp to display date */
export function formatDate(value: string | number | Date, locale = 'en-IN'): string {
  return new Intl.DateTimeFormat(locale, { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(value))
}

/** Format to datetime */
export function formatDateTime(value: string | number | Date, locale = 'en-IN'): string {
  return new Intl.DateTimeFormat(locale, {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  }).format(new Date(value))
}

/** Relative time (e.g. "2 hours ago") */
export function formatRelativeTime(value: string | number | Date, locale = 'en-IN'): string {
  const ms      = new Date(value).getTime()
  const diff    = (ms - Date.now()) / 1000
  const rtf     = new Intl.RelativeTimeFormat(locale, { numeric: 'auto' })
  const absDiff = Math.abs(diff)
  if (absDiff < 60)    return rtf.format(Math.round(diff), 'second')
  if (absDiff < 3600)  return rtf.format(Math.round(diff / 60), 'minute')
  if (absDiff < 86400) return rtf.format(Math.round(diff / 3600), 'hour')
  if (absDiff < 2592000) return rtf.format(Math.round(diff / 86400), 'day')
  return formatDate(value, locale)
}

/** Format duration in ms to human-readable */
export function formatDuration(ms: number): string {
  const s = Math.floor(ms / 1000)
  if (s < 60)   return `${s}s`
  if (s < 3600) return `${Math.floor(s / 60)}m ${s % 60}s`
  return `${Math.floor(s / 3600)}h ${Math.floor((s % 3600) / 60)}m`
}

/** Format number with locale separators */
export function formatNumber(n: number, locale = 'en-IN', opts?: Intl.NumberFormatOptions): string {
  return new Intl.NumberFormat(locale, opts).format(n)
}

/** Format currency (default INR) */
export function formatCurrency(n: number, currency = 'INR', locale = 'en-IN'): string {
  return new Intl.NumberFormat(locale, { style: 'currency', currency }).format(n)
}

/** Format percentage */
export function formatPercent(n: number, decimals = 1): string {
  return `${(n * 100).toFixed(decimals)}%`
}

/** Truncate string to maxLen with ellipsis */
export function truncate(str: string, maxLen: number): string {
  return str.length > maxLen ? `${str.slice(0, maxLen - 1)}…` : str
}

/** Title-case a string */
export function titleCase(str: string): string {
  return str.replace(/\w\S*/g, (t) => t.charAt(0).toUpperCase() + t.slice(1).toLowerCase())
}

/** Sentence-case */
export function sentenceCase(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
}

/** Convert snake_case / UPPER_CASE to readable label */
export function labelFromKey(key: string): string {
  return key.replace(/_/g, ' ').replace(/([A-Z])/g, ' $1').trim().replace(/\s+/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase())
}

/** Format file size (re-export for convenience) */
export { formatFileSize } from '../file'
