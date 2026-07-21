// ─── Notification Slice ───────────────────────────────────────────────────────
import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { NotificationItem } from '@/types/ui'

interface NotificationState {
  items:    NotificationItem[]
  maxQueue: number
}

const initialState: NotificationState = {
  items:    [],
  maxQueue: 5,
}

const notificationSlice = createSlice({
  name: 'notifications',
  initialState,
  reducers: {
    addNotification(state, action: PayloadAction<Omit<NotificationItem, 'id' | 'createdAt'>>) {
      const item: NotificationItem = {
        ...action.payload,
        id:        crypto.randomUUID(),
        createdAt: Date.now(),
        duration:  action.payload.duration ?? 4000,
      }
      // Cap the queue at maxQueue to prevent overflow
      if (state.items.length >= state.maxQueue) state.items.shift()
      state.items.push(item)
    },
    dismissNotification(state, action: PayloadAction<string>) {
      state.items = state.items.filter((n) => n.id !== action.payload)
    },
    clearAllNotifications(state) {
      state.items = []
    },
  },
})

export const {
  addNotification,
  dismissNotification,
  clearAllNotifications,
} = notificationSlice.actions

export const notificationReducer = notificationSlice.reducer

// ─── Notification Selectors ────────────────────────────────────────────────────
import type { RootState } from '../index'

export const selectNotifications = (s: RootState) => s.notifications.items
export const selectNotificationCount = (s: RootState) => s.notifications.items.length
