export const truncate    = (s: string, n = 80) => s.length > n ? s.slice(0, n) + '…' : s
export const toTitleCase = (s: string) => s.replace(/\w\S*/g, (t) => t[0].toUpperCase() + t.slice(1).toLowerCase())
export const slugify     = (s: string) => s.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '')
