import React, { createContext, useContext, useEffect, useState } from 'react'

export type ThemeName =
  | 'light'
  | 'dark'
  | 'high-contrast'
  | 'court'
  | 'government'
  | 'research'
  | 'district-court'
  | 'high-court'
  | 'supreme-court'

export type DensityMode = 'compact' | 'default' | 'comfortable'

interface ThemeContextValue {
  theme: ThemeName
  density: DensityMode
  setTheme: (theme: ThemeName) => void
  setDensity: (density: DensityMode) => void
  isDark: boolean
}

const ThemeContext = createContext<ThemeContextValue | null>(null)

interface ThemeProviderProps {
  children: React.ReactNode
  defaultTheme?: ThemeName
  defaultDensity?: DensityMode
  storageKey?: string
}

export function ThemeProvider({
  children,
  defaultTheme = 'light',
  defaultDensity = 'default',
  storageKey = 'tc-theme',
}: ThemeProviderProps) {
  const [theme, setThemeState] = useState<ThemeName>(() => {
    return (localStorage.getItem(storageKey) as ThemeName) ?? defaultTheme
  })
  const [density, setDensityState] = useState<DensityMode>(() => {
    return (localStorage.getItem(`${storageKey}-density`) as DensityMode) ?? defaultDensity
  })

  useEffect(() => {
    const root = document.documentElement
    root.setAttribute('data-theme', theme)
    root.setAttribute('data-density', density)
    localStorage.setItem(storageKey, theme)
    localStorage.setItem(`${storageKey}-density`, density)
  }, [theme, density, storageKey])

  const setTheme = (newTheme: ThemeName) => setThemeState(newTheme)
  const setDensity = (newDensity: DensityMode) => setDensityState(newDensity)

  return (
    <ThemeContext.Provider
      value={{ theme, density, setTheme, setDensity, isDark: theme === 'dark' }}
    >
      {children}
    </ThemeContext.Provider>
  )
}

export function useTheme(): ThemeContextValue {
  const ctx = useContext(ThemeContext)
  if (!ctx) throw new Error('useTheme must be used within ThemeProvider')
  return ctx
}
