// ─── Admin Court API ──────────────────────────────────────────────────────────
// Connects to CourtManagementController at /admin/courts
// Backend field names are used verbatim — do NOT rename them.
import { springApi } from '@/api/client/springApiClient'
import type { ApiResponse } from '@/types/api'
import type { PageResponse } from './adminUserApi'

// ── Court Types ───────────────────────────────────────────────────────────────

/** Matches CourtResponse.java from the backend */
export interface CourtResponse {
  uuid: string
  courtCode: string
  courtName: string
  courtType: string   // HIGH_COURT | DISTRICT_COURT | SESSIONS_COURT | MAGISTRATE_COURT | TRIBUNAL
  state: string
  district: string
  address: string
  phoneNumber: string | null
  email: string | null
  isActive: boolean
  createdAt: string
  updatedAt: string | null
}

/** Matches CourtRequest.java — courtCode and courtName are @NotBlank */
export interface CourtRequest {
  courtCode: string
  courtName: string
  courtType: string
  state: string
  district: string
  address: string
  phoneNumber?: string
  email?: string
  isActive?: boolean
}

// ── Bench Types ───────────────────────────────────────────────────────────────

/** Matches CourtBenchResponse.java */
export interface BenchResponse {
  uuid: string
  courtUuid: string
  courtName: string
  benchNumber: string
  benchType: string   // SINGLE | DIVISION | FULL | SPECIAL
  description: string | null
  isActive: boolean
  createdAt: string
}

/** Matches CourtBenchRequest.java — courtUuid and benchNumber are @NotBlank */
export interface BenchRequest {
  courtUuid: string
  benchNumber: string
  benchType: string
  description?: string
  isActive?: boolean
}

// ── Court Room Types ──────────────────────────────────────────────────────────

/** Matches CourtRoomResponse.java */
export interface CourtRoomResponse {
  uuid: string
  courtUuid: string
  courtName: string
  roomNumber: string
  floor: string | null
  capacity: number | null
  hasVideoConferencing: boolean
  isActive: boolean
  createdAt: string
}

/** Matches CourtRoomRequest.java — courtUuid and roomNumber are @NotBlank */
export interface CourtRoomRequest {
  courtUuid: string
  roomNumber: string
  floor?: string
  capacity?: number
  hasVideoConferencing?: boolean
  isActive?: boolean
}

// ── Court Type Enum Values ────────────────────────────────────────────────────
export const COURT_TYPES = [
  { value: 'HIGH_COURT',        label: 'High Court' },
  { value: 'DISTRICT_COURT',    label: 'District Court' },
  { value: 'SESSIONS_COURT',    label: 'Sessions Court' },
  { value: 'MAGISTRATE_COURT',  label: 'Magistrate Court' },
  { value: 'TRIBUNAL',          label: 'Tribunal' },
] as const

export const BENCH_TYPES = [
  { value: 'SINGLE',   label: 'Single Bench' },
  { value: 'DIVISION', label: 'Division Bench' },
  { value: 'FULL',     label: 'Full Bench' },
  { value: 'SPECIAL',  label: 'Special Bench' },
] as const

// ── API Functions ──────────────────────────────────────────────────────────────
export const adminCourtApi = {

  // ── Courts ─────────────────────────────────────────────────────────────────
  listCourts: (
    page = 0,
    size = 20,
    sortBy = 'courtName',
    direction: 'asc' | 'desc' = 'asc',
  ) =>
    springApi.get<ApiResponse<PageResponse<CourtResponse>>>('/admin/courts', {
      params: { page, size, sortBy, direction },
    }),

  getCourtByUuid: (uuid: string) =>
    springApi.get<ApiResponse<CourtResponse>>(`/admin/courts/${uuid}`),

  createCourt: (data: CourtRequest) =>
    springApi.post<ApiResponse<CourtResponse>>('/admin/courts', data),

  updateCourt: (uuid: string, data: CourtRequest) =>
    springApi.put<ApiResponse<CourtResponse>>(`/admin/courts/${uuid}`, data),

  deleteCourt: (uuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/courts/${uuid}`),

  // ── Benches ────────────────────────────────────────────────────────────────
  getCourtBenches: (courtUuid: string) =>
    springApi.get<ApiResponse<BenchResponse[]>>(`/admin/courts/${courtUuid}/benches`),

  createBench: (data: BenchRequest) =>
    springApi.post<ApiResponse<BenchResponse>>('/admin/courts/benches', data),

  updateBench: (benchUuid: string, data: BenchRequest) =>
    springApi.put<ApiResponse<BenchResponse>>(`/admin/courts/benches/${benchUuid}`, data),

  deleteBench: (benchUuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/courts/benches/${benchUuid}`),

  // ── Court Rooms ────────────────────────────────────────────────────────────
  getCourtRooms: (courtUuid: string) =>
    springApi.get<ApiResponse<CourtRoomResponse[]>>(`/admin/courts/${courtUuid}/rooms`),

  createRoom: (data: CourtRoomRequest) =>
    springApi.post<ApiResponse<CourtRoomResponse>>('/admin/courts/rooms', data),

  updateRoom: (roomUuid: string, data: CourtRoomRequest) =>
    springApi.put<ApiResponse<CourtRoomResponse>>(`/admin/courts/rooms/${roomUuid}`, data),

  deleteRoom: (roomUuid: string) =>
    springApi.delete<ApiResponse<void>>(`/admin/courts/rooms/${roomUuid}`),
}
