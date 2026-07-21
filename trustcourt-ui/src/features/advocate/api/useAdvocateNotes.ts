import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getCaseNotes, createCaseNote, deleteCaseNote, CreateCaseNoteRequest } from '@/api/services/advocate/noteApi'
import { ADVOCATE_CASES_QUERY_KEY } from './useAdvocateCases'

export const useAdvocateNotes = (caseUuid: string, page = 0, size = 10) => {
  return useQuery({
    queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'notes', caseUuid, page, size],
    queryFn: () => getCaseNotes(caseUuid, page, size),
    enabled: !!caseUuid
  })
}

export const useCreateCaseNote = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ caseUuid, request }: { caseUuid: string, request: CreateCaseNoteRequest }) => createCaseNote(caseUuid, request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'notes', variables.caseUuid] })
    }
  })
}

export const useDeleteCaseNote = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ caseUuid, noteUuid }: { caseUuid: string, noteUuid: string }) => deleteCaseNote(caseUuid, noteUuid),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [...ADVOCATE_CASES_QUERY_KEY, 'notes', variables.caseUuid] })
    }
  })
}
