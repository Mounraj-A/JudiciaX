// â”€â”€â”€ NotificationContext â€” Phase F3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import React, { createContext, useContext, useReducer, useCallback } from 'react'
import type { Notification, NotificationState } from '@/types/shared/notification'
import { v4 as uuid } from 'uuid'

type Action =
  | { type: 'ADD';         payload: Notification }
  | { type: 'MARK_READ';   payload: string }
  | { type: 'MARK_ALL_READ' }
  | { type: 'REMOVE';      payload: string }
  | { type: 'SET_ITEMS';   payload: Notification[] }
  | { type: 'TOGGLE_DRAWER' }
  | { type: 'SET_LOADING'; payload: boolean }

function reducer(state: NotificationState, action: Action): NotificationState {
  switch (action.type) {
    case 'ADD':         return { ...state, items: [action.payload, ...state.items], unreadCount: state.unreadCount + 1 }
    case 'MARK_READ':   return { ...state, items: state.items.map((n) => n.id === action.payload ? { ...n, read: true } : n), unreadCount: Math.max(0, state.unreadCount - 1) }
    case 'MARK_ALL_READ': return { ...state, items: state.items.map((n) => ({ ...n, read: true })), unreadCount: 0 }
    case 'REMOVE':      return { ...state, items: state.items.filter((n) => n.id !== action.payload) }
    case 'SET_ITEMS':   return { ...state, items: action.payload, unreadCount: action.payload.filter((n) => !n.read).length }
    case 'TOGGLE_DRAWER': return { ...state, isOpen: !state.isOpen }
    case 'SET_LOADING': return { ...state, isLoading: action.payload }
    default:            return state
  }
}

interface NotificationContextValue {
  state:         NotificationState
  add:           (n: Omit<Notification, 'id' | 'read' | 'createdAt'>) => void
  markRead:      (id: string) => void
  markAllRead:   () => void
  remove:        (id: string) => void
  setItems:      (items: Notification[]) => void
  toggleDrawer:  () => void
}

const NotificationContext = createContext<NotificationContextValue | null>(null)

export function NotificationProvider({ children }: { children: React.ReactNode }) {
  const [state, dispatch] = useReducer(reducer, {
    items: [], unreadCount: 0, isOpen: false, isLoading: false,
  })

  const add        = useCallback((n: Omit<Notification, 'id' | 'read' | 'createdAt'>) =>
    dispatch({ type: 'ADD', payload: { ...n, id: uuid(), read: false, createdAt: new Date().toISOString() } }), [])
  const markRead   = useCallback((id: string)           => dispatch({ type: 'MARK_READ', payload: id }), [])
  const markAllRead= useCallback(()                     => dispatch({ type: 'MARK_ALL_READ' }), [])
  const remove     = useCallback((id: string)           => dispatch({ type: 'REMOVE', payload: id }), [])
  const setItems   = useCallback((items: Notification[]) => dispatch({ type: 'SET_ITEMS', payload: items }), [])
  const toggleDrawer= useCallback(()                    => dispatch({ type: 'TOGGLE_DRAWER' }), [])

  return (
    <NotificationContext.Provider value={{ state, add, markRead, markAllRead, remove, setItems, toggleDrawer }}>
      {children}
    </NotificationContext.Provider>
  )
}

export function useNotificationContext() {
  const ctx = useContext(NotificationContext)
  if (!ctx) throw new Error('useNotificationContext must be inside <NotificationProvider>')
  return ctx
}
