import { useQuery } from '@tanstack/react-query'
import { getCaseOrders, getCaseJudgements } from '@/api/services/advocate/orderApi'
import { ADVOCATE_CASES_QUERY_KEY } from './useAdvocateCases'

export const useAdvocateOrders = (caseUuid: string, page = 0, size = 10) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'orders', caseUuid, page, size],
    queryFn: () => getCaseOrders(caseUuid, page, size),
    enabled: !!caseUuid
  })
}

export const useAdvocateJudgements = (caseUuid: string, page = 0, size = 10) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'judgements', caseUuid, page, size],
    queryFn: () => getCaseJudgements(caseUuid, page, size),
    enabled: !!caseUuid
  })
}
