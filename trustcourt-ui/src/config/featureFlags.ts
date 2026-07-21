// ─── Feature Flags ────────────────────────────────────────────────────────────
// All flags start disabled. Enable as phases are implemented.

export interface FeatureFlags {
  DARK_MODE:           boolean
  AI_VISUALIZATION:    boolean
  RESEARCH_MODE:       boolean
  BULK_OPERATIONS:     boolean
  DOCUMENT_OCR:        boolean
  AI_EXPLANATIONS:     boolean
  ADVANCED_ANALYTICS:  boolean
  EXPORT_PDF:          boolean
  MULTI_LANGUAGE:      boolean
  HIGH_CONTRAST:       boolean
  PERFORMANCE_MONITOR: boolean
  NOTIFICATIONS_PUSH:  boolean
}

const defaultFlags: FeatureFlags = {
  DARK_MODE:           true,
  AI_VISUALIZATION:    false,  // Phase F4
  RESEARCH_MODE:       false,  // Phase F5
  BULK_OPERATIONS:     false,  // Phase F2
  DOCUMENT_OCR:        false,  // Phase F3
  AI_EXPLANATIONS:     false,  // Phase F4
  ADVANCED_ANALYTICS:  false,  // Phase F5
  EXPORT_PDF:          false,  // Phase F3
  MULTI_LANGUAGE:      false,  // Phase F1+
  HIGH_CONTRAST:       true,
  PERFORMANCE_MONITOR: false,  // Production only
  NOTIFICATIONS_PUSH:  false,  // Phase F2
}

// Runtime override from env
const runtimeFlags: Partial<FeatureFlags> = {}
try {
  const raw = import.meta.env.VITE_FEATURE_FLAGS
  if (raw) Object.assign(runtimeFlags, JSON.parse(raw) as Partial<FeatureFlags>)
} catch { /* silent */ }

export const featureFlags: Readonly<FeatureFlags> = Object.freeze({
  ...defaultFlags,
  ...runtimeFlags,
})

export const isFeatureEnabled = (flag: keyof FeatureFlags): boolean =>
  featureFlags[flag] === true
