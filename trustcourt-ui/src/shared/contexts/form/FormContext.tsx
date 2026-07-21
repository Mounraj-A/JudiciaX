// ─── FormContext — Phase F3 ───────────────────────────────────────────────────
import React, { createContext, useContext, useReducer, useCallback } from 'react'
import type { Validator } from '@/shared/utils/validation'

interface FieldState {
  value:    unknown
  error:    string | null
  touched:  boolean
  dirty:    boolean
}

interface FormState {
  fields:     Record<string, FieldState>
  isSubmitting:boolean
  isValid:    boolean
  submitError:string | null
}

type FormAction =
  | { type: 'SET_VALUE';   field: string; value: unknown }
  | { type: 'SET_ERROR';   field: string; error: string | null }
  | { type: 'SET_TOUCHED'; field: string }
  | { type: 'RESET' }
  | { type: 'SET_SUBMITTING'; value: boolean }
  | { type: 'SET_SUBMIT_ERROR'; error: string | null }

function reducer(state: FormState, action: FormAction): FormState {
  switch (action.type) {
    case 'SET_VALUE':
      return { ...state, fields: { ...state.fields, [action.field]: { ...state.fields[action.field], value: action.value, dirty: true, error: null } } }
    case 'SET_ERROR':
      return { ...state, fields: { ...state.fields, [action.field]: { ...state.fields[action.field], error: action.error } } }
    case 'SET_TOUCHED':
      return { ...state, fields: { ...state.fields, [action.field]: { ...state.fields[action.field], touched: true } } }
    case 'RESET':
      return { fields: {}, isSubmitting: false, isValid: true, submitError: null }
    case 'SET_SUBMITTING':
      return { ...state, isSubmitting: action.value }
    case 'SET_SUBMIT_ERROR':
      return { ...state, submitError: action.error }
    default: return state
  }
}

interface FormContextValue {
  state:      FormState
  register:   (field: string, validators?: Validator[]) => void
  setValue:   (field: string, value: unknown) => void
  setTouched: (field: string) => void
  getField:   (field: string) => FieldState
  getValues:  () => Record<string, unknown>
  reset:      () => void
  setSubmitError: (error: string | null) => void
}

const FormContext = createContext<FormContextValue | null>(null)

const validatorsRegistry: Record<string, Validator[]> = {}

export function FormProvider({ children }: { children: React.ReactNode }) {
  const [state, dispatch] = useReducer(reducer, { fields: {}, isSubmitting: false, isValid: true, submitError: null })

  const register = useCallback((field: string, validators: Validator[] = []) => {
    validatorsRegistry[field] = validators
  }, [])

  const setValue = useCallback((field: string, value: unknown) => {
    dispatch({ type: 'SET_VALUE', field, value })
    const vds = validatorsRegistry[field] ?? []
    for (const v of vds) {
      const err = v(String(value ?? ''))
      if (err) { dispatch({ type: 'SET_ERROR', field, error: err }); return }
    }
    dispatch({ type: 'SET_ERROR', field, error: null })
  }, [])

  const setTouched = useCallback((field: string) => dispatch({ type: 'SET_TOUCHED', field }), [])
  const reset      = useCallback(() => { dispatch({ type: 'RESET' }); Object.keys(validatorsRegistry).forEach((k) => delete validatorsRegistry[k]) }, [])
  const getField   = useCallback((field: string) => state.fields[field] ?? { value: '', error: null, touched: false, dirty: false }, [state.fields])
  const getValues  = useCallback(() => Object.fromEntries(Object.entries(state.fields).map(([k, v]) => [k, v.value])), [state.fields])
  const setSubmitError = useCallback((error: string | null) => dispatch({ type: 'SET_SUBMIT_ERROR', error }), [])

  return (
    <FormContext.Provider value={{ state, register, setValue, setTouched, getField, getValues, reset, setSubmitError }}>
      {children}
    </FormContext.Provider>
  )
}

export function useFormContext(): FormContextValue {
  const ctx = useContext(FormContext)
  if (!ctx) throw new Error('useFormContext must be inside <FormProvider>')
  return ctx
}
