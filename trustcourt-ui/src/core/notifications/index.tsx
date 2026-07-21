// ─── Notification & Toast Infrastructure ──────────────────────────────────────
import { useAppSelector, useAppDispatch } from '@/store'
import { selectNotifications, dismissNotification } from '@/store/slices/notificationSlice'
import { Alert } from '@/shared/design-system/components/feedback'
import { motion, AnimatePresence } from 'framer-motion'

/**
 * Global Toast Manager rendering notification queue from Redux `notificationSlice`.
 */
export function ToastManager() {
  const notifications = useAppSelector(selectNotifications)
  const dispatch      = useAppDispatch()

  return (
    <div
      className="fixed bottom-6 right-6 z-[var(--tc-z-toast,700)] flex w-full max-w-sm flex-col gap-3 pointer-events-none"
      role="region"
      aria-label="Application Notifications"
    >
      <AnimatePresence>
        {notifications.map((item) => (
          <motion.div
            key={item.id}
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, scale: 0.9, transition: { duration: 0.15 } }}
            className="pointer-events-auto shadow-large rounded-lg"
          >
            <Alert
              variant={item.type === 'error' ? 'danger' : item.type}
              title={item.title}
              onDismiss={() => dispatch(dismissNotification(item.id))}
            >
              {item.message}
            </Alert>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  )
}
