import { useQuery } from '@tanstack/react-query'
import { getCourts, getCaseCategories, getCaseTypes, getCaseCategoriesByType } from './masterApi'

export const MASTER_QUERY_KEY = ['master']

export const useCourts = () => {
  return useQuery({
    queryKey: [...MASTER_QUERY_KEY, 'courts'],
    queryFn: () => getCourts()
  })
}

export const useCaseCategories = () => {
  return useQuery({
    queryKey: [...MASTER_QUERY_KEY, 'case-categories'],
    queryFn: () => getCaseCategories()
  })
}

export const useCaseTypes = () => {
  return useQuery({
    queryKey: [...MASTER_QUERY_KEY, 'case-types'],
    queryFn: () => getCaseTypes()
  })
}

export const useCaseCategoriesByType = (caseTypeUuid: string | undefined) => {
  return useQuery({
    queryKey: [...MASTER_QUERY_KEY, 'case-categories', caseTypeUuid],
    queryFn: () => {
      if (!caseTypeUuid) return []
      return getCaseCategoriesByType(caseTypeUuid)
    },
    enabled: !!caseTypeUuid
  })
}
