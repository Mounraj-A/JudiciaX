// TrustCourt Radius, Shadows, Breakpoints, Animation, and Extended Tokens

export const radius = {
  none:   '0px',
  sm:     '0.25rem',  //  4px
  DEFAULT:'0.375rem', //  6px
  md:     '0.5rem',   //  8px
  lg:     '0.75rem',  // 12px
  xl:     '1rem',     // 16px
  '2xl':  '1.5rem',   // 24px
  '3xl':  '2rem',     // 32px
  full:   '9999px',
  // Per-element
  button:  '0.5rem',
  input:   '0.5rem',
  card:    '0.75rem',
  dialog:  '1rem',
  drawer:  '0',
  table:   '0.5rem',
  badge:   '9999px',
  chip:    '9999px',
} as const

export const shadows = {
  none:     'none',
  soft:     '0 1px 3px 0 rgba(0,0,0,0.06), 0 1px 2px 0 rgba(0,0,0,0.04)',
  DEFAULT:  '0 1px 3px 0 rgba(0,0,0,0.1), 0 1px 2px 0 rgba(0,0,0,0.06)',
  medium:   '0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06)',
  large:    '0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05)',
  floating: '0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04)',
  overlay:  '0 25px 50px -12px rgba(0,0,0,0.25)',
  inner:    'inset 0 2px 4px 0 rgba(0,0,0,0.06)',
  // AI module card glow effects (very subtle)
  'ai-jpi': '0 0 0 1px rgba(234,88,12,0.2), 0 4px 12px rgba(234,88,12,0.08)',
  'ai-cts': '0 0 0 1px rgba(15,118,110,0.2), 0 4px 12px rgba(15,118,110,0.08)',
  'ai-xai': '0 0 0 1px rgba(109,40,217,0.2), 0 4px 12px rgba(109,40,217,0.08)',
} as const

export const breakpoints = {
  sm:   '640px',
  md:   '768px',
  lg:   '1024px',
  xl:   '1280px',
  '2xl':'1536px',
  '3xl':'1920px',   // Ultra-wide
} as const

export const animation = {
  keyframes: {
    shimmer: {
      '0%':   { backgroundPosition: '-200% 0' },
      '100%': { backgroundPosition: '200% 0'  },
    },
    'fade-in': {
      '0%':   { opacity: '0' },
      '100%': { opacity: '1' },
    },
    'slide-up': {
      '0%':   { opacity: '0', transform: 'translateY(8px)' },
      '100%': { opacity: '1', transform: 'translateY(0)'   },
    },
    'slide-down': {
      '0%':   { opacity: '0', transform: 'translateY(-8px)' },
      '100%': { opacity: '1', transform: 'translateY(0)'    },
    },
    'scale-in': {
      '0%':   { opacity: '0', transform: 'scale(0.96)' },
      '100%': { opacity: '1', transform: 'scale(1)'    },
    },
    'pulse-soft': {
      '0%, 100%': { opacity: '1'   },
      '50%':      { opacity: '0.5' },
    },
  },
  animations: {
    shimmer:      'shimmer 2s linear infinite',
    'fade-in':    'fade-in 0.2s ease-out',
    'slide-up':   'slide-up 0.2s ease-out',
    'slide-down': 'slide-down 0.2s ease-out',
    'scale-in':   'scale-in 0.15s ease-out',
    'pulse-soft': 'pulse-soft 2s ease-in-out infinite',
  },
  duration: {
    instant: '0ms',
    fast:    '100ms',
    normal:  '200ms',
    slow:    '300ms',
    slower:  '500ms',
  },
  easing: {
    default:   'cubic-bezier(0.4, 0, 0.2, 1)',
    in:        'cubic-bezier(0.4, 0, 1, 1)',
    out:       'cubic-bezier(0, 0, 0.2, 1)',
    spring:    'cubic-bezier(0.34, 1.56, 0.64, 1)',
  },
} as const

// Extended tokens
export const focusRing = {
  width:  '2px',
  offset: '2px',
  style:  'solid',
  color:  'var(--tc-color-secondary-600)',
} as const

export const density = {
  compact:     { factor: 0.75, label: 'Compact'     },
  default:     { factor: 1.0,  label: 'Default'     },
  comfortable: { factor: 1.25, label: 'Comfortable' },
} as const

export const surfaceLevels = {
  0: '#FFFFFF',
  1: '#F8F9FA',
  2: '#F1F3F5',
  3: '#E9ECEF',
  4: '#DEE2E6',
} as const

export const zIndex = {
  base:     0,
  elevated: 10,
  dropdown: 100,
  sticky:   200,
  overlay:  300,
  modal:    400,
  popover:  500,
  tooltip:  600,
  toast:    700,
  max:      9999,
} as const

export const borderWidth = {
  none:   '0px',
  thin:   '1px',
  medium: '2px',
  thick:  '4px',
} as const

export const blur = {
  none: '0',
  sm:   '4px',
  md:   '8px',
  lg:   '12px',
  xl:   '16px',
} as const

export const iconSizes = {
  xs:   '12px',
  sm:   '16px',
  md:   '20px',
  lg:   '24px',
  xl:   '32px',
  '2xl':'40px',
} as const
