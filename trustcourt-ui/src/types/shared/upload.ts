// ─── Upload Types — Phase F3 ──────────────────────────────────────────────────

export type UploadStatus = 'idle' | 'queued' | 'uploading' | 'success' | 'error' | 'cancelled' | 'retrying'

export interface UploadFile {
  id:          string   // uuid
  file:        File
  name:        string
  size:        number
  mimeType:    string
  extension:   string
  status:      UploadStatus
  progress:    number   // 0-100
  error?:      string
  previewUrl?: string   // object URL for images
  uploadedUrl?:string   // returned from server
  retryCount:  number
  addedAt:     number   // timestamp
}

export interface UploadConfig {
  accept?:         string[]    // MIME types e.g. ['image/*', 'application/pdf']
  maxFileSize?:    number      // bytes
  maxFiles?:       number
  maxTotalSize?:   number      // bytes
  autoUpload?:     boolean
  maxRetries?:     number
  chunkSize?:      number      // for chunked upload
  uploadFn:        UploadFn   // caller provides the actual upload implementation
}

export type UploadFn = (
  file: File,
  onProgress: (pct: number) => void,
  signal:     AbortSignal,
) => Promise<{ url: string; [key: string]: unknown }>

export interface UploadState {
  queue:     UploadFile[]
  uploading: boolean
  totalProgress: number // 0-100 (average of all active)
}

export interface UploadValidationError {
  file:    File
  reason: 'file_too_large' | 'invalid_type' | 'too_many_files' | 'total_size_exceeded'
  detail:  string
}
