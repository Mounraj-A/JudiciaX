import { springApiClient } from '@/api/client/springApiClient'
import type { ApiResponse, PaginatedResponse as PageResponse } from '@/types/api'

// ── Types ─────────────────────────────────────────────────────────────

export interface CaseSummaryResponse {
  uuid: string
  caseNumber: string
  caseType: string
  caseCategory: string
  status: string
  filingDate: string
  lastHearingDate?: string
  nextHearingDate?: string
  petitionerName: string
  respondentName: string
}

export interface CaseResponse extends CaseSummaryResponse {
  description: string
  priority: string
  courtRoom?: string
  judgeName?: string
  createdAt: string
  updatedAt: string
}

export interface CaseTimelineResponse {
  uuid: string
  eventType: string
  eventTitle: string
  eventDescription: string
  eventDate: string
  actorName: string
  actorRole: string
}

export interface CreateCaseRequest {
  caseTitle: string
  caseDescription?: string
  caseType: string
  petitionerName: string
  respondentName: string
  filingDate?: string
  courtUuid?: string
  caseCategoryUuid?: string
  respondentAdvocateUuid?: string
  policeStation?: string
  actSection?: string
  priority?: string
}

export interface UpdateCaseRequest {
  description?: string
  reliefSought?: string
  isUrgent?: boolean
}

// ── API Functions ───────────────────────────────────────────────────────

export const createCase = async (request: CreateCaseRequest): Promise<CaseResponse> => {
  const { data } = await springApiClient.post<ApiResponse<CaseResponse>>('/advocate/cases', request)
  return data.data
}

export const getMyCases = async (page = 0, size = 10, sortBy = 'createdAt', direction = 'desc'): Promise<PageResponse<CaseSummaryResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<CaseSummaryResponse>>>('/advocate/cases', {
    params: { page, size, sortBy, direction }
  })
  return data.data
}

export const getCaseByUuid = async (caseUuid: string): Promise<CaseResponse> => {
  const { data } = await springApiClient.get<ApiResponse<CaseResponse>>(`/advocate/cases/${caseUuid}`)
  return data.data
}

export const updateCase = async (caseUuid: string, request: UpdateCaseRequest): Promise<CaseResponse> => {
  const { data } = await springApiClient.put<ApiResponse<CaseResponse>>(`/advocate/cases/${caseUuid}`, request)
  return data.data
}

export const getTimeline = async (caseUuid: string): Promise<CaseTimelineResponse[]> => {
  const { data } = await springApiClient.get<ApiResponse<CaseTimelineResponse[]>>(`/advocate/cases/${caseUuid}/timeline`)
  return data.data
}

export const getCasesByStatus = async (status: string, page = 0, size = 10): Promise<PageResponse<CaseSummaryResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<CaseSummaryResponse>>>(`/advocate/cases/status/${status}`, {
    params: { page, size }
  })
  return data.data
}

export const searchCases = async (keyword: string, page = 0, size = 10): Promise<PageResponse<CaseSummaryResponse>> => {
  const { data } = await springApiClient.get<ApiResponse<PageResponse<CaseSummaryResponse>>>('/advocate/cases/search', {
    params: { keyword, page, size }
  })
  return data.data
}

// ── Wizard Endpoints ──────────────────────────────────────────────────

export interface PartyRequest {
  partyName: string
  partyType: string // PETITIONER, RESPONDENT
  isPrimary?: boolean
  partyCategory?: string // INDIVIDUAL, ORGANIZATION
  gender?: string
  dateOfBirth?: string
  aadhaarNumber?: string
  phoneNumber?: string
  email?: string
  partyAddress?: string
  district?: string
  state?: string
  pinCode?: string
  occupation?: string
  representativeName?: string
}

export const addCaseParty = async (caseUuid: string, request: PartyRequest): Promise<void> => {
  await springApiClient.post(`/advocate/cases/${caseUuid}/parties`, request)
}

export interface LegalInfoRequest {
  policeStation?: string
  firNumber?: string
  crimeNumber?: string
  previousCaseNumber?: string
  acts?: string
  sections?: string
  rules?: string
  articles?: string
  legalProvisions?: string
  precedentReferences?: string
}

export const saveLegalInfo = async (caseUuid: string, request: LegalInfoRequest): Promise<void> => {
  await springApiClient.post(`/advocate/cases/${caseUuid}/legal-info`, request)
}

export interface PaymentRequest {
  caseUuid: string
  amount: number
  paymentMethod: string
}

export const processPayment = async (request: PaymentRequest): Promise<string> => {
  const { data } = await springApiClient.post<ApiResponse<string>>('/advocate/payments', request)
  return data.data
}

export const submitCase = async (caseUuid: string): Promise<CaseResponse> => {
  const { data } = await springApiClient.put<ApiResponse<CaseResponse>>(`/advocate/cases/${caseUuid}/submit`)
  return data.data
}
