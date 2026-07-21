// TrustCourt Enterprise Color Tokens
// All colors follow the Deep Navy judicial design language.

export const colors = {
  // ─── Judicial Primary: Deep Navy ─────────────────────────────────────────
  primary: {
    50:  '#EFF3FB',
    100: '#D9E3F5',
    200: '#B5C8EB',
    300: '#8AABDC',
    400: '#5F8DCC',
    500: '#3A70BC',
    600: '#1E4FA0',
    700: '#0F1D3A',  // Deep Navy – main
    800: '#0B1629',
    900: '#080F1C',
    950: '#040810',
  },
  // ─── Secondary: Royal Blue ────────────────────────────────────────────────
  secondary: {
    50:  '#EFF6FF',
    100: '#DBEAFE',
    200: '#BFDBFE',
    300: '#93C5FD',
    400: '#60A5FA',
    500: '#3B82F6',
    600: '#1E3A8A',  // Royal Blue – main
    700: '#1E40AF',
    800: '#1E3A8A',
    900: '#1E3366',
    950: '#172554',
  },
  // ─── Accent: Judicial Orange ──────────────────────────────────────────────
  accent: {
    50:  '#FFF7ED',
    100: '#FFEDD5',
    200: '#FED7AA',
    300: '#FDBA74',
    400: '#FB923C',
    500: '#F97316',
    600: '#C2410C',  // Judicial Orange – main
    700: '#9A3412',
    800: '#7C2D12',
    900: '#431407',
    950: '#1C0A03',
  },
  // ─── Semantic States ──────────────────────────────────────────────────────
  success: {
    DEFAULT: '#059669',
    light: '#D1FAE5',
    50: '#ECFDF5',
    500: '#10B981',
    600: '#059669',
    700: '#047857',
  },
  warning: {
    DEFAULT: '#D97706',
    light: '#FEF3C7',
    50: '#FFFBEB',
    500: '#F59E0B',
    600: '#D97706',
    700: '#B45309',
  },
  danger: {
    DEFAULT: '#B91C1C',
    light: '#FEE2E2',
    50: '#FEF2F2',
    500: '#EF4444',
    600: '#DC2626',
    700: '#B91C1C',
  },
  info: {
    DEFAULT: '#0369A1',
    light: '#E0F2FE',
    50: '#F0F9FF',
    500: '#0EA5E9',
    600: '#0284C7',
    700: '#0369A1',
  },
  // ─── Surface & Background ─────────────────────────────────────────────────
  surface: {
    0: '#FFFFFF',   // Background
    1: '#F8F9FA',   // Surface / Light Gray
    2: '#F1F3F5',   // Elevated surface
    3: '#E9ECEF',   // Card surface
    4: '#DEE2E6',   // Divider level
  },
  // ─── Neutral / Gray ───────────────────────────────────────────────────────
  neutral: {
    50:  '#F9FAFB',
    100: '#F3F4F6',
    200: '#E5E7EB',
    300: '#D1D5DB',
    400: '#9CA3AF',
    500: '#6B7280',
    600: '#4B5563',
    700: '#374151',
    800: '#1F2937',
    900: '#111827',
    950: '#030712',
  },
  // ─── AI Module Colors ─────────────────────────────────────────────────────
  ai: {
    ocr:      { DEFAULT: '#0891B2', light: '#CFFAFE', dark: '#0E7490' },   // Cyan
    nlp:      { DEFAULT: '#7C3AED', light: '#EDE9FE', dark: '#6D28D9' },   // Purple
    feature:  { DEFAULT: '#4338CA', light: '#E0E7FF', dark: '#3730A3' },   // Indigo
    jpi:      { DEFAULT: '#EA580C', light: '#FFEDD5', dark: '#C2410C' },   // Orange
    cts:      { DEFAULT: '#0F766E', light: '#CCFBF1', dark: '#0D6B63' },   // Teal
    xai:      { DEFAULT: '#6D28D9', light: '#EDE9FE', dark: '#5B21B6' },   // Violet
    decision: { DEFAULT: '#1D4ED8', light: '#DBEAFE', dark: '#1E40AF' },   // Deep Blue
    governance:{ DEFAULT: '#475569', light: '#F1F5F9', dark: '#334155' },  // Slate
  },
  // ─── Priority Level Tokens ────────────────────────────────────────────────
  priority: {
    emergency: '#B91C1C',  // JPI ≥ 90
    critical:  '#EA580C',  // JPI 75–89
    high:      '#D97706',  // JPI 60–74
    medium:    '#0369A1',  // JPI 40–59
    low:       '#059669',  // JPI < 40
  },
  // ─── Trust Level Tokens ───────────────────────────────────────────────────
  trust: {
    high:     '#059669',   // CTS ≥ 80
    moderate: '#D97706',   // CTS 50–79
    low:      '#B91C1C',   // CTS < 50
  },
} as const

export type Colors = typeof colors
