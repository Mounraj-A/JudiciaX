// ─── UploadContext — Phase F3 ─────────────────────────────────────────────────
import React, { createContext, useContext } from 'react'
import { useUpload } from '@/shared/hooks/useUpload'
import type { UploadConfig } from '@/types/shared/upload'

type UploadContextValue = ReturnType<typeof useUpload>
const UploadContext = createContext<UploadContextValue | null>(null)

interface UploadProviderProps extends UploadConfig { children: React.ReactNode }

export function UploadProvider({ children, ...config }: UploadProviderProps) {
  const upload = useUpload(config)
  return <UploadContext.Provider value={upload}>{children}</UploadContext.Provider>
}

export function useUploadContext(): UploadContextValue {
  const ctx = useContext(UploadContext)
  if (!ctx) throw new Error('useUploadContext must be inside <UploadProvider>')
  return ctx
}
