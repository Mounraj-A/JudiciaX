/**
 * Future Integration
 * Backend APIs for Court ↔ Judge Assignment are not yet available.
 * This service layer defines the TypeScript interfaces and contracts 
 * required by the frontend UI. It will be implemented with actual 
 * HTTP calls (and matching React Query hooks) once the backend endpoints 
 * are added.
 */

export interface CourtJudgeResponse {
  uuid: string;
  judgeIdNumber: string;
  judgeName: string;
  designation: string;
  specialization: string;
  totalAssignedCases: number;
  isActive: boolean;
}

export interface AssignJudgeToCourtRequest {
  courtUuid: string;
  judgeUserUuid: string;
}

export interface AssignJudgeToBenchRequest {
  benchUuid: string;
  judgeUserUuid: string;
}

// TODO: Implement the following methods when backend is available:
// export const courtOrganizationService = {
//   listJudgesForCourt: async (courtUuid: string): Promise<CourtJudgeResponse[]> => { ... },
//   listJudgesForBench: async (benchUuid: string): Promise<CourtJudgeResponse[]> => { ... },
//   assignJudgeToCourt: async (data: AssignJudgeToCourtRequest): Promise<void> => { ... },
//   removeJudgeFromCourt: async (courtUuid: string, judgeUuid: string): Promise<void> => { ... },
//   assignJudgeToBench: async (data: AssignJudgeToBenchRequest): Promise<void> => { ... },
//   removeJudgeFromBench: async (benchUuid: string, judgeUuid: string): Promise<void> => { ... },
// }
