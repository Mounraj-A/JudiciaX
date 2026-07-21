import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getMyCases, searchCases, getCaseByUuid, createCase, getCasesByStatus, getTimeline, CreateCaseRequest } from '@/api/services/advocate/caseApi'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '@/constants/routes'

export const ADVOCATE_CASES_QUERY_KEY = ['advocate-cases']

export const useAdvocateCases = (page: number, size: number, sortBy?: string, direction?: string) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'list', page, size, sortBy, direction],
    queryFn: () => getMyCases(page, size, sortBy, direction)
  })
}

export const useSearchCases = (keyword: string, page: number, size: number) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'search', keyword, page, size],
    queryFn: () => searchCases(keyword, page, size),
    enabled: !!keyword
  })
}

export const useCaseDetails = (caseUuid: string) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'details', caseUuid],
    queryFn: () => getCaseByUuid(caseUuid),
    enabled: !!caseUuid
  })
}

export const useCaseTimeline = (caseUuid: string) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'timeline', caseUuid],
    queryFn: () => getTimeline(caseUuid),
    enabled: !!caseUuid
  })
}

export const useAdvocateCasesByStatus = (status: string, page: number, size: number) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'status', status, page, size],
    queryFn: () => getCasesByStatus(status, page, size)
  })
}

export const useCreateCase = () => {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (request: CreateCaseRequest) => createCase(request),
    onSuccess: (data) => {
      alert('Case filed successfully')
      queryClient.invalidateQueries({ queryKey: ADVOCATE_CASES_QUERY_KEY })
      navigate(ROUTES.ADVOCATE.CASE.replace(':id', data.uuid))
    },
    onError: (error: Error) => {
      alert(error.message || 'Failed to file case')
    }
  })
}

export const useAddCaseParty = () => {
  return useMutation({
    mutationFn: ({ caseUuid, request }: { caseUuid: string; request: import('@/api/services/advocate/caseApi').PartyRequest }) => 
      import('@/api/services/advocate/caseApi').then(m => m.addCaseParty(caseUuid, request))
  })
}

export const useSaveLegalInfo = () => {
  return useMutation({
    mutationFn: ({ caseUuid, request }: { caseUuid: string; request: import('@/api/services/advocate/caseApi').LegalInfoRequest }) => 
      import('@/api/services/advocate/caseApi').then(m => m.saveLegalInfo(caseUuid, request))
  })
}

export const useProcessPayment = () => {
  return useMutation({
    mutationFn: (request: import('@/api/services/advocate/caseApi').PaymentRequest) => 
      import('@/api/services/advocate/caseApi').then(m => m.processPayment(request))
  })
}

export const useSubmitCase = () => {
  return useMutation({
    mutationFn: (caseUuid: string) => 
      import('@/api/services/advocate/caseApi').then(m => m.submitCase(caseUuid))
  })
}
