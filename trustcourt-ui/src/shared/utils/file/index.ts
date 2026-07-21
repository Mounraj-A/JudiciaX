// ─── File Utilities — Phase F3 ────────────────────────────────────────────────

export const FILE_SIZE_UNITS = ['B', 'KB', 'MB', 'GB', 'TB'] as const

/** Format bytes to human-readable string */
export function formatFileSize(bytes: number, decimals = 1): string {
  if (bytes === 0) return '0 B'
  const k    = 1024
  const i    = Math.min(Math.floor(Math.log(bytes) / Math.log(k)), FILE_SIZE_UNITS.length - 1)
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(decimals))} ${FILE_SIZE_UNITS[i]}`
}

/** Get file extension from filename */
export function getExtension(filename: string): string {
  return filename.slice((filename.lastIndexOf('.') >>> 0) + 1).toLowerCase()
}

/** Get basename without extension */
export function getBasename(filename: string): string {
  const name = filename.split('/').pop() ?? filename
  const dot  = name.lastIndexOf('.')
  return dot > 0 ? name.slice(0, dot) : name
}

/** MIME type → friendly label */
export function getMimeLabel(mime: string): string {
  const map: Record<string, string> = {
    'application/pdf': 'PDF', 'image/jpeg': 'JPEG', 'image/png': 'PNG',
    'image/webp': 'WebP', 'image/gif': 'GIF', 'image/svg+xml': 'SVG',
    'text/plain': 'Text', 'text/csv': 'CSV',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'Excel',
    'application/vnd.ms-excel': 'Excel', 'application/msword': 'Word',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'Word',
    'application/zip': 'ZIP', 'application/json': 'JSON',
  }
  return map[mime] ?? mime.split('/')[1]?.toUpperCase() ?? 'File'
}

/** Whether a MIME type is an image */
export const isImage = (mime: string) => mime.startsWith('image/')

/** Whether a MIME type is a PDF */
export const isPDF   = (mime: string) => mime === 'application/pdf'

/** Whether a MIME type is a video */
export const isVideo = (mime: string) => mime.startsWith('video/')

/** MIME → icon name (used with icon registry) */
export function getFileIcon(mime: string): string {
  if (isImage(mime)) return 'image'
  if (isPDF(mime))   return 'pdf'
  if (isVideo(mime)) return 'video'
  if (mime.includes('spreadsheet') || mime.includes('excel')) return 'spreadsheet'
  if (mime.includes('word')) return 'document'
  if (mime.includes('zip') || mime.includes('archive')) return 'archive'
  return 'file'
}

/** Validate file against accepted MIME types (supports wildcards like image/*) */
export function isAcceptedMime(mime: string, accept: string[]): boolean {
  return accept.some((pattern) => {
    if (pattern === '*' || pattern === '*/*') return true
    if (pattern.endsWith('/*')) return mime.startsWith(pattern.slice(0, -2))
    return mime === pattern
  })
}
