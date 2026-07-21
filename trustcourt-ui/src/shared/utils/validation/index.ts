// ─── Validation Utilities — Phase F3 ─────────────────────────────────────────

export type Validator<T = string> = (value: T) => string | null

/** Required field */
export const required: Validator = (v) => !v?.trim() ? 'This field is required' : null

/** Email format */
export const email: Validator = (v) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? null : 'Enter a valid email address'

/** Minimum length */
export const minLength = (min: number): Validator => (v) =>
  v.length >= min ? null : `Minimum ${min} characters required`

/** Maximum length */
export const maxLength = (max: number): Validator => (v) =>
  v.length <= max ? null : `Maximum ${max} characters allowed`

/** Minimum numeric value */
export const min = (n: number): Validator<number> => (v) =>
  v >= n ? null : `Minimum value is ${n}`

/** Maximum numeric value */
export const max = (n: number): Validator<number> => (v) =>
  v <= n ? null : `Maximum value is ${n}`

/** Regex pattern match */
export const pattern = (regex: RegExp, message: string): Validator => (v) =>
  regex.test(v) ? null : message

/** Password strength — returns null if strong enough */
export const passwordStrength: Validator = (v) => {
  if (v.length < 8)               return 'Password must be at least 8 characters'
  if (!/[A-Z]/.test(v))           return 'Password must contain an uppercase letter'
  if (!/[a-z]/.test(v))           return 'Password must contain a lowercase letter'
  if (!/[0-9]/.test(v))           return 'Password must contain a number'
  return null
}

/** Get password strength 0-4 */
export function getPasswordStrength(v: string): 0 | 1 | 2 | 3 | 4 {
  let score = 0
  if (v.length >= 8)           score++
  if (/[A-Z]/.test(v))         score++
  if (/[a-z]/.test(v))         score++
  if (/[0-9]/.test(v))         score++
  if (/[^A-Za-z0-9]/.test(v))  score = Math.min(4, score + 1)
  return score as 0 | 1 | 2 | 3 | 4
}

/** Phone number (India) */
export const indianPhone: Validator = (v) =>
  /^[6-9]\d{9}$/.test(v.replace(/\D/g, '')) ? null : 'Enter a valid 10-digit mobile number'

/** Run multiple validators, return first error */
export function validate<T>(value: T, validators: Validator<T>[]): string | null {
  for (const fn of validators) {
    const err = fn(value)
    if (err) return err
  }
  return null
}
