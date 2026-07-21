// â”€â”€â”€ UploadProgress â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import type { UploadFile } from '@/types/shared/upload'
import { formatFileSize } from '@/shared/utils/file'

interface UploadProgressProps {
  files:      UploadFile[]
  onCancel?:  (id: string) => void
  onRetry?:   (id: string) => void
  onRemove?:  (id: string) => void
  className?: string
}

export function UploadProgress({ files, onCancel, onRetry, onRemove, className = '' }: UploadProgressProps) {
  if (files.length === 0) return null

  return (
    <div className={className} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
      {files.map((file) => (
        <div key={file.id} style={{
          display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem',
          background: '#FFFFFF', border: '1px solid #E5E7EB', borderRadius: '0.5rem'
        }}>
          {file.previewUrl ? (
            <img src={file.previewUrl} alt={file.name} style={{ width: 40, height: 40, objectFit: 'cover', borderRadius: '0.25rem' }} />
          ) : (
            <div style={{ width: 40, height: 40, background: '#F3F4F6', borderRadius: '0.25rem', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#9CA3AF', fontSize: '0.75rem', fontWeight: 600 }}>
              {file.extension.toUpperCase() || 'FILE'}
            </div>
          )}

          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
              <span style={{ fontSize: '0.875rem', fontWeight: 500, color: '#111827', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{file.name}</span>
              <span style={{ fontSize: '0.75rem', color: '#6B7280' }}>{formatFileSize(file.size)}</span>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <div style={{ flex: 1, height: 4, background: '#E5E7EB', borderRadius: 2, overflow: 'hidden' }}>
                <div style={{
                  height: '100%', width: `${file.progress}%`, borderRadius: 2,
                  background: file.status === 'success' ? '#065F46' : file.status === 'error' ? '#DC2626' : '#0F1D3A',
                  transition: 'width 0.3s ease'
                }} />
              </div>
              <span style={{ fontSize: '0.75rem', fontWeight: 600, color: file.status === 'error' ? '#DC2626' : '#374151', minWidth: '3ch', textAlign: 'right' }}>
                {file.status === 'success' ? 'âœ“' : file.status === 'error' ? 'âš ' : `${Math.round(file.progress)}%`}
              </span>
            </div>
            {file.error && <p style={{ fontSize: '0.75rem', color: '#DC2626', margin: '0.25rem 0 0' }}>{file.error}</p>}
          </div>

          <div style={{ display: 'flex', gap: '0.25rem' }}>
            {file.status === 'uploading' && onCancel && (
              <button onClick={() => onCancel(file.id)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#6B7280', fontSize: '1rem', padding: '0.25rem' }} title="Cancel">âœ•</button>
            )}
            {file.status === 'error' && onRetry && (
              <button onClick={() => onRetry(file.id)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#0F1D3A', fontSize: '1rem', padding: '0.25rem' }} title="Retry">â†»</button>
            )}
            {(file.status === 'success' || file.status === 'error' || file.status === 'cancelled') && onRemove && (
              <button onClick={() => onRemove(file.id)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#6B7280', fontSize: '1rem', padding: '0.25rem' }} title="Remove">âœ•</button>
            )}
          </div>
        </div>
      ))}
    </div>
  )
}
