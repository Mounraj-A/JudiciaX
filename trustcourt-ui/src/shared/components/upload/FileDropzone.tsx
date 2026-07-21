// ─── FileDropzone — Phase F3 ──────────────────────────────────────────────────
import React, { useCallback, useState, useRef } from 'react'

export interface FileDropzoneProps {
  onFilesSelected: (files: File[]) => void
  accept?:         string
  maxFiles?:       number
  multiple?:       boolean
  disabled?:       boolean
  className?:      string
  title?:          string
  subtitle?:       string
}

export function FileDropzone({
  onFilesSelected, accept, maxFiles, multiple = true, disabled = false, className = '',
  title = 'Click or drag files here to upload',
  subtitle = 'Supported files: all standard formats'
}: FileDropzoneProps) {
  const [isDragActive, setIsDragActive] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)

  const handleDragEnter = useCallback((e: React.DragEvent) => {
    e.preventDefault(); e.stopPropagation(); if (!disabled) setIsDragActive(true)
  }, [disabled])
  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault(); e.stopPropagation(); setIsDragActive(false)
  }, [])
  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault(); e.stopPropagation(); if (!disabled) setIsDragActive(true)
  }, [disabled])

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault(); e.stopPropagation(); setIsDragActive(false)
    if (disabled) return
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const files = Array.from(e.dataTransfer.files)
      if (maxFiles && files.length > maxFiles) {
        onFilesSelected(files.slice(0, maxFiles)) // Or handle error
      } else {
        onFilesSelected(files)
      }
      e.dataTransfer.clearData()
    }
  }, [disabled, onFilesSelected, maxFiles])

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      onFilesSelected(Array.from(e.target.files))
      if (inputRef.current) inputRef.current.value = ''
    }
  }, [onFilesSelected])

  return (
    <div
      className={className}
      onDragEnter={handleDragEnter}
      onDragLeave={handleDragLeave}
      onDragOver={handleDragOver}
      onDrop={handleDrop}
      onClick={() => !disabled && inputRef.current?.click()}
      style={{
        border: `2px dashed ${isDragActive ? '#0F1D3A' : disabled ? '#E5E7EB' : '#D1D5DB'}`,
        borderRadius: '0.75rem',
        background: isDragActive ? '#F3F6FF' : disabled ? '#F9FAFB' : '#FFFFFF',
        padding: '2rem 1.5rem',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        textAlign: 'center',
        cursor: disabled ? 'not-allowed' : 'pointer',
        transition: 'all 0.2s ease',
        opacity: disabled ? 0.7 : 1
      }}
    >
      <input
        type="file"
        ref={inputRef}
        onChange={handleChange}
        accept={accept}
        multiple={multiple}
        disabled={disabled}
        style={{ display: 'none' }}
      />
      <div style={{
        width: 48, height: 48, borderRadius: '50%', background: isDragActive ? '#DBEAFE' : '#F3F4F6',
        display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '1rem',
        color: isDragActive ? '#1E40AF' : '#6B7280', fontSize: '1.5rem'
      }}>
        ↑
      </div>
      <p style={{ fontSize: '1rem', fontWeight: 600, color: '#111827', margin: '0 0 0.25rem 0' }}>{title}</p>
      <p style={{ fontSize: '0.8125rem', color: '#6B7280', margin: 0 }}>{subtitle}</p>
    </div>
  )
}
