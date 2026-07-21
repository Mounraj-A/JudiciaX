import { springApiClient } from '@/api/client/springApiClient'


export interface Court {
  uuid: string
  courtName: string
  courtCode: string
  courtType: string
  state: string
  city: string
}

export interface CaseType {
  uuid: string
  typeName: string
  typeCode: string
  description?: string
}

export interface CaseCategory {
  uuid: string
  caseTypeUuid?: string
  caseTypeCode?: string
  categoryName: string
  categoryCode: string
  description?: string
}

export const getCourts = async (): Promise<Court[]> => {
  const { data } = await springApiClient.get<Court[]>('/master/courts')
  return data
}

export const getCaseCategories = async (): Promise<CaseCategory[]> => {
  const { data } = await springApiClient.get<CaseCategory[]>('/master/case-categories')
  return data
}

export const getCaseTypes = async (): Promise<CaseType[]> => {
  const { data } = await springApiClient.get<CaseType[]>('/master/case-types')
  return data
}

export const getCaseCategoriesByType = async (caseTypeUuid: string): Promise<CaseCategory[]> => {
  const { data } = await springApiClient.get<CaseCategory[]>(`/master/case-types/${caseTypeUuid}/categories`)
  return data
}
