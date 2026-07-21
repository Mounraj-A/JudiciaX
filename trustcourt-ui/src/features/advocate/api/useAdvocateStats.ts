import { useQuery } from '@tanstack/react-query'
import { getCaseStatistics } from '@/api/services/advocate/statsApi'
import { ADVOCATE_CASES_QUERY_KEY } from './useAdvocateCases'

export const useAdvocateStats = () => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'statistics'],
    queryFn: () => getCaseStatistics()
  })
}
