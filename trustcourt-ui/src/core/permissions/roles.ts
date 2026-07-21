// ─── Role Enum — Phase F2 ─────────────────────────────────────────────────────
// Enum values align with UserRole union type in types/auth/index.ts

export enum Role {
  JUDGE       = 'JUDGE',
  ADVOCATE    = 'ADVOCATE',
  CLERK       = 'CLERK',
  ADMIN       = 'ADMIN',
  SUPER_ADMIN = 'SUPER_ADMIN',
  AUDITOR     = 'AUDITOR',
  AI_OPERATOR = 'AI_OPERATOR',
  RESEARCHER  = 'RESEARCHER',
  VIEWER      = 'VIEWER',
}
