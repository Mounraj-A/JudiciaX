// ─── Admin Court Hooks ────────────────────────────────────────────────────────
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  adminCourtApi,
  type CourtRequest,
  type BenchRequest,
  type CourtRoomRequest,
} from '../api/adminCourtApi'
import { queryKeys } from '@/lib/queryClient'

// ── Court Queries ──────────────────────────────────────────────────────────────

export function useAdminCourts(
  page = 0,
  size = 20,
  sortBy = 'courtName',
  direction: 'asc' | 'desc' = 'asc',
) {
  return useQuery({
    queryKey: queryKeys.courts.all({ page, size, sortBy, direction }),
    queryFn:  () => adminCourtApi.listCourts(page, size, sortBy, direction),
    select:   (res) => res.data,
    placeholderData: (prev) => prev, // keep previous data while fetching next page
  })
}

export function useAdminCourt(uuid: string) {
  return useQuery({
    queryKey: queryKeys.courts.byId(uuid),
    queryFn:  () => adminCourtApi.getCourtByUuid(uuid),
    enabled:  !!uuid,
    select:   (res) => res.data,
  })
}

export function useCourtBenches(courtUuid: string) {
  return useQuery({
    queryKey: queryKeys.courts.benches(courtUuid),
    queryFn:  () => adminCourtApi.getCourtBenches(courtUuid),
    enabled:  !!courtUuid,
    select:   (res) => res.data,
  })
}

export function useCourtRooms(courtUuid: string) {
  return useQuery({
    queryKey: queryKeys.courts.rooms(courtUuid),
    queryFn:  () => adminCourtApi.getCourtRooms(courtUuid),
    enabled:  !!courtUuid,
    select:   (res) => res.data,
  })
}

// ── Court Mutations ────────────────────────────────────────────────────────────

export function useCreateCourt() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CourtRequest) => adminCourtApi.createCourt(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['courts'] }),
  })
}

export function useUpdateCourt() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: CourtRequest }) =>
      adminCourtApi.updateCourt(uuid, data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['courts'] })
      qc.invalidateQueries({ queryKey: queryKeys.courts.byId(variables.uuid) })
    },
  })
}

export function useDeleteCourt() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (uuid: string) => adminCourtApi.deleteCourt(uuid),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['courts'] }),
  })
}

// ── Bench Mutations ────────────────────────────────────────────────────────────

export function useCreateBench() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: BenchRequest) => adminCourtApi.createBench(data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: queryKeys.courts.benches(variables.courtUuid) })
      qc.invalidateQueries({ queryKey: ['courts'] }) // refresh court list (bench count)
    },
  })
}

export function useUpdateBench() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: BenchRequest }) =>
      adminCourtApi.updateBench(uuid, data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: queryKeys.courts.benches(variables.data.courtUuid) })
    },
  })
}

export function useDeleteBench() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ benchUuid, courtUuid: _courtUuid }: { benchUuid: string; courtUuid: string }) =>
      adminCourtApi.deleteBench(benchUuid),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: queryKeys.courts.benches(variables.courtUuid) })
      qc.invalidateQueries({ queryKey: ['courts'] })
    },
  })
}

// ── Court Room Mutations ───────────────────────────────────────────────────────

export function useCreateRoom() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CourtRoomRequest) => adminCourtApi.createRoom(data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: queryKeys.courts.rooms(variables.courtUuid) })
      qc.invalidateQueries({ queryKey: ['courts'] })
    },
  })
}

export function useUpdateRoom() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ uuid, data }: { uuid: string; data: CourtRoomRequest }) =>
      adminCourtApi.updateRoom(uuid, data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: queryKeys.courts.rooms(variables.data.courtUuid) })
    },
  })
}

export function useDeleteRoom() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ roomUuid, courtUuid: _courtUuid }: { roomUuid: string; courtUuid: string }) =>
      adminCourtApi.deleteRoom(roomUuid),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: queryKeys.courts.rooms(variables.courtUuid) })
      qc.invalidateQueries({ queryKey: ['courts'] })
    },
  })
}
