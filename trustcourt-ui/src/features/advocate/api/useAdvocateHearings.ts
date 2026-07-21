import { useQuery } from '@tanstack/react-query'
import { getMyHearings, getHearingDetails } from '@/api/services/advocate/hearingApi'

export const ADVOCATE_HEARINGS_QUERY_KEY = ['advocate-hearings']

export const useAdvocateHearings = (page: number, size: number, sortBy?: string, direction?: string) => {
  return useQuery({
    queryKey: [...ADVOCATE_HEARINGS_QUERY_KEY, 'list', page, size, sortBy, direction],
    queryFn: () => getMyHearings(page, size, sortBy, direction)
  })
}

export const useHearingDetails = (hearingUuid: string) => {
  return useQuery({
    queryKey: [...ADVOCATE_HEARINGS_QUERY_KEY, 'details', hearingUuid],
    queryFn: () => getHearingDetails(hearingUuid),
    enabled: !!hearingUuid
  })
}
