// ─── Mock Sessions — Dev/Test Infrastructure ──────────────────────────────────
import type { SessionMetadata } from '@/types/auth/session'

export const mockSession: SessionMetadata = {
  loginTime:    new Date(Date.now() - 10 * 60 * 1000).toISOString(), // 10 min ago
  lastActivity: Date.now() - 60_000,
  rememberMe:   false,
  browserName:  'Chrome',
  deviceType:   'desktop',
}

export const mockMobileSession: SessionMetadata = {
  ...mockSession,
  deviceType: 'mobile',
  browserName: 'Safari',
}
