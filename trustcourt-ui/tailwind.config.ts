/** @type {import('tailwindcss').Config} */
import type { Config } from 'tailwindcss'
import { colors } from './src/shared/design-system/tokens/colors'
import { spacing } from './src/shared/design-system/tokens/spacing'
import { typography } from './src/shared/design-system/tokens/typography'
import { radius } from './src/shared/design-system/tokens/radius'
import { shadows } from './src/shared/design-system/tokens/shadows'
import { animation } from './src/shared/design-system/tokens/animation'
import { breakpoints } from './src/shared/design-system/tokens/breakpoints'

const config: Config = {
  darkMode: ['class'],
  content: [
    './index.html',
    './src/**/*.{ts,tsx,js,jsx}',
  ],
  theme: {
    extend: {
      colors: {
        // Judicial Palette
        primary: {
          DEFAULT: colors.primary[600],
          50: colors.primary[50],
          100: colors.primary[100],
          200: colors.primary[200],
          300: colors.primary[300],
          400: colors.primary[400],
          500: colors.primary[500],
          600: colors.primary[600],
          700: colors.primary[700],
          800: colors.primary[800],
          900: colors.primary[900],
          950: colors.primary[950],
        },
        secondary: {
          DEFAULT: colors.secondary[600],
          ...colors.secondary,
        },
        accent: {
          DEFAULT: colors.accent[600],
          ...colors.accent,
        },
        success: colors.success,
        warning: colors.warning,
        danger: colors.danger,
        info: colors.info,
        surface: colors.surface,
        // AI Module Colors
        ai: colors.ai,
        // Semantic
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        ring: 'hsl(var(--ring))',
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        muted: {
          DEFAULT: 'hsl(var(--muted))',
          foreground: 'hsl(var(--muted-foreground))',
        },
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))',
        },
        popover: {
          DEFAULT: 'hsl(var(--popover))',
          foreground: 'hsl(var(--popover-foreground))',
        },
        destructive: {
          DEFAULT: 'hsl(var(--destructive))',
          foreground: 'hsl(var(--destructive-foreground))',
        },
      },
      spacing: spacing.scale,
      fontFamily: {
        sans: typography.fontFamily.sans,
        mono: typography.fontFamily.mono,
        display: typography.fontFamily.display,
      },
      fontSize: typography.fontSize,
      fontWeight: typography.fontWeight,
      lineHeight: typography.lineHeight,
      letterSpacing: typography.letterSpacing,
      borderRadius: {
        ...radius,
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 2px)',
        sm: 'calc(var(--radius) - 4px)',
      },
      boxShadow: shadows,
      screens: breakpoints,
      keyframes: {
        ...animation.keyframes,
        'accordion-down': {
          from: { height: '0' },
          to: { height: 'var(--radix-accordion-content-height)' },
        },
        'accordion-up': {
          from: { height: 'var(--radix-accordion-content-height)' },
          to: { height: '0' },
        },
      },
      animation: {
        ...animation.animations,
        'accordion-down': 'accordion-down 0.2s ease-out',
        'accordion-up': 'accordion-up 0.2s ease-out',
      },
    },
  },
  plugins: [],
}

export default config
