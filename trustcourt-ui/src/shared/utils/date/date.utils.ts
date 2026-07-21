import dayjs from 'dayjs'
import relativeTimePlugin from 'dayjs/plugin/relativeTime'
dayjs.extend(relativeTimePlugin)
export const formatDate      = (d: string) => dayjs(d).format('MMM DD, YYYY')
export const getRelativeTime = (d: string) => dayjs(d).fromNow()
export const isOverdue       = (d: string) => dayjs().isAfter(dayjs(d))
