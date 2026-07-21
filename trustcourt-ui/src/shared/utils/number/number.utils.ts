export const formatScore  = (n: number, decimals = 1) => n.toFixed(decimals)
export const clamp        = (n: number, min: number, max: number) => Math.min(max, Math.max(min, n))
export const percentage   = (val: number, total: number) => total === 0 ? 0 : (val / total) * 100
