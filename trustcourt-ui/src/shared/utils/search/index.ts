// ─── Search Utilities — Phase F3 ──────────────────────────────────────────────

/** Tokenize a query string into terms */
export function tokenize(query: string): string[] {
  return query.toLowerCase().trim().split(/\s+/).filter(Boolean)
}

/** Simple substring match (case-insensitive) */
export function matches(text: string, query: string): boolean {
  return text.toLowerCase().includes(query.toLowerCase().trim())
}

/** All tokens must match at least one field */
export function matchesAll(fields: string[], query: string): boolean {
  const tokens = tokenize(query)
  return tokens.every((token) => fields.some((field) => field.toLowerCase().includes(token)))
}

/** Any token matches any field */
export function matchesAny(fields: string[], query: string): boolean {
  const tokens = tokenize(query)
  return tokens.some((token) => fields.some((field) => field.toLowerCase().includes(token)))
}

/** Highlight query terms in text — returns HTML string */
export function highlight(text: string, query: string, tag = 'mark'): string {
  if (!query.trim()) return text
  const tokens  = tokenize(query)
  let result    = text
  for (const token of tokens) {
    const regex = new RegExp(`(${escapeRegex(token)})`, 'gi')
    result = result.replace(regex, `<${tag}>$1</${tag}>`)
  }
  return result
}

/** Fuzzy score — higher = better match (simple Levenshtein-based) */
export function fuzzyScore(text: string, query: string): number {
  const t = text.toLowerCase()
  const q = query.toLowerCase()
  if (t === q)          return 1
  if (t.startsWith(q))  return 0.9
  if (t.includes(q))    return 0.7
  return 0
}

/** Filter array by search query over specified string fields */
export function filterBySearch<T>(
  items:  T[],
  query:  string,
  getFields: (item: T) => string[],
): T[] {
  if (!query.trim()) return items
  return items.filter((item) => matchesAll(getFields(item), query))
}

function escapeRegex(str: string): string {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}
