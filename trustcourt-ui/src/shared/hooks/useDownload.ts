// ─── useDownload — Phase F3 ───────────────────────────────────────────────────
import { useState, useCallback } from 'react'

export function useDownload() {
  const [isDownloading, setIsDownloading] = useState(false)
  const [error, setError]                 = useState<string | null>(null)

  /** Download a Blob or string content as a file */
  const downloadBlob = useCallback((blob: Blob, filename: string) => {
    const url = URL.createObjectURL(blob)
    const a   = document.createElement('a')
    a.href     = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  }, [])

  const downloadText = useCallback((content: string, filename: string, mimeType = 'text/plain') => {
    downloadBlob(new Blob([content], { type: mimeType }), filename)
  }, [downloadBlob])

  /** Download from a URL (e.g. presigned S3 URL) */
  const downloadUrl = useCallback(async (url: string, filename: string) => {
    setIsDownloading(true)
    setError(null)
    try {
      const res  = await fetch(url)
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const blob = await res.blob()
      downloadBlob(blob, filename)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Download failed')
    } finally {
      setIsDownloading(false)
    }
  }, [downloadBlob])

  return { isDownloading, error, downloadBlob, downloadText, downloadUrl }
}
