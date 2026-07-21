// ─── useUpload — Phase F3 ─────────────────────────────────────────────────────
import { useState, useCallback, useRef } from 'react'
import { v4 as uuid } from 'uuid'
import type { UploadFile, UploadConfig, UploadState, UploadValidationError } from '@/types/shared/upload'

export function useUpload(config: UploadConfig) {
  const {
    accept, maxFileSize, maxFiles = 10, maxTotalSize,
    autoUpload = true, maxRetries = 3, uploadFn,
  } = config

  const [queue, setQueue]         = useState<UploadFile[]>([])
  const abortRefs                 = useRef<Map<string, AbortController>>(new Map())

  const updateFile = useCallback((id: string, patch: Partial<UploadFile>) => {
    setQueue((prev) => prev.map((f) => f.id === id ? { ...f, ...patch } : f))
  }, [])

  const validate = useCallback((files: File[]): { valid: File[]; errors: UploadValidationError[] } => {
    const errors: UploadValidationError[] = []
    const valid: File[] = []
    for (const file of files) {
      if (maxFileSize && file.size > maxFileSize) {
        errors.push({ file, reason: 'file_too_large', detail: `Max ${maxFileSize} bytes` })
        continue
      }
      if (accept && !accept.some((t) => t === '*' || file.type.match(new RegExp(t.replace('*', '.*'))))) {
        errors.push({ file, reason: 'invalid_type', detail: `Expected ${accept.join(', ')}` })
        continue
      }
      valid.push(file)
    }
    const existing = queue.reduce((sum, f) => sum + f.size, 0)
    if (maxTotalSize && valid.reduce((s, f) => s + f.size, existing) > maxTotalSize) {
      errors.push(...valid.map((file) => ({ file, reason: 'total_size_exceeded' as const, detail: 'Total size limit exceeded' })))
      return { valid: [], errors }
    }
    return { valid, errors }
  }, [accept, maxFileSize, maxTotalSize, queue])

  const addFiles = useCallback(async (files: File[]) => {
    const { valid, errors } = validate(files)
    if (errors.length) console.warn('[useUpload] Validation errors:', errors)
    const newItems: UploadFile[] = await Promise.all(valid.map(async (file) => {
      const previewUrl = file.type.startsWith('image/') ? URL.createObjectURL(file) : undefined
      return {
        id: uuid(), file, name: file.name, size: file.size,
        mimeType: file.type, extension: file.name.split('.').pop() ?? '',
        status: 'queued' as const, progress: 0, retryCount: 0,
        addedAt: Date.now(), previewUrl,
      }
    }))
    setQueue((prev) => [...prev, ...newItems].slice(0, maxFiles))
    if (autoUpload) newItems.forEach((f) => uploadFile(f.id))
    return errors
  }, [validate, maxFiles, autoUpload]) // eslint-disable-line react-hooks/exhaustive-deps

  const uploadFile = useCallback(async (id: string) => {
    const item = queue.find((f) => f.id === id) ?? null
    if (!item) return
    const controller = new AbortController()
    abortRefs.current.set(id, controller)
    updateFile(id, { status: 'uploading', progress: 0 })
    try {
      const result = await uploadFn(item.file, (pct) => updateFile(id, { progress: pct }), controller.signal)
      updateFile(id, { status: 'success', progress: 100, uploadedUrl: result.url })
    } catch (e) {
      if ((e as Error).name === 'AbortError') {
        updateFile(id, { status: 'cancelled' })
      } else {
        const retry = (item.retryCount ?? 0)
        if (retry < (maxRetries ?? 3)) {
          updateFile(id, { status: 'retrying', retryCount: retry + 1 })
          setTimeout(() => uploadFile(id), 1000 * (retry + 1))
        } else {
          updateFile(id, { status: 'error', error: (e as Error).message })
        }
      }
    } finally {
      abortRefs.current.delete(id)
    }
  }, [queue, uploadFn, maxRetries, updateFile])

  const cancelFile = useCallback((id: string) => {
    abortRefs.current.get(id)?.abort()
    updateFile(id, { status: 'cancelled' })
  }, [updateFile])

  const removeFile = useCallback((id: string) => {
    cancelFile(id)
    setQueue((prev) => {
      const f = prev.find((x) => x.id === id)
      if (f?.previewUrl) URL.revokeObjectURL(f.previewUrl)
      return prev.filter((x) => x.id !== id)
    })
  }, [cancelFile])

  const retryFile = useCallback((id: string) => {
    updateFile(id, { status: 'queued', progress: 0, error: undefined, retryCount: 0 })
    uploadFile(id)
  }, [updateFile, uploadFile])

  const clearCompleted = useCallback(() => {
    setQueue((prev) => prev.filter((f) => f.status !== 'success' && f.status !== 'error'))
  }, [])

  const totalProgress = queue.length === 0 ? 0
    : Math.round(queue.reduce((s, f) => s + f.progress, 0) / queue.length)

  const state: UploadState = {
    queue,
    uploading: queue.some((f) => f.status === 'uploading'),
    totalProgress,
  }

  return { state, queue, addFiles, uploadFile, cancelFile, removeFile, retryFile, clearCompleted }
}
