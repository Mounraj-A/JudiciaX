package com.courtai.reports.mapper;

import com.courtai.advocate.entity.Advocate;
import com.courtai.clerk.entity.Clerk;
import com.courtai.court.entity.Court;
import com.courtai.judge.entity.Judge;
import com.courtai.reports.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting domain entities to report response DTOs.
 *
 * <p>Only maps basic identity and profile fields from each entity.
 * Aggregate counts and computed metrics are populated by the service layer.</p>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {

    // ── Court ─────────────────────────────────────────────────────────────

    @Mapping(target = "courtId",   source = "id")
    @Mapping(target = "courtUuid", source = "uuid")
    @Mapping(target = "courtName", source = "courtName")
    @Mapping(target = "courtCode", source = "courtCode")
    @Mapping(target = "courtType", source = "courtType")
    @Mapping(target = "state",     source = "state")
    @Mapping(target = "district",  source = "district")
    @Mapping(target = "isActive",  source = "isActive")
    CourtReportResponse toCourtReport(Court court);

    List<CourtReportResponse> toCourtReportList(List<Court> courts);

    // ── Judge ─────────────────────────────────────────────────────────────

    @Mapping(target = "judgeUuid",        source = "uuid")
    @Mapping(target = "judgeName",        source = "user.username")
    @Mapping(target = "judgeIdNumber",    source = "judgeIdNumber")
    @Mapping(target = "designation",      source = "designation")
    @Mapping(target = "specialization",   source = "specialization")
    @Mapping(target = "courtName",        source = "courtName")
    @Mapping(target = "yearsOfExperience",source = "yearsOfExperience")
    JudgeReportResponse toJudgeReport(Judge judge);

    List<JudgeReportResponse> toJudgeReportList(List<Judge> judges);

    // ── Advocate ──────────────────────────────────────────────────────────

    @Mapping(target = "advocateUuid",      source = "uuid")
    @Mapping(target = "advocateName",      source = "user.username")
    @Mapping(target = "barCouncilNumber",  source = "barCouncilNumber")
    @Mapping(target = "stateBarCouncil",   source = "stateBarCouncil")
    @Mapping(target = "specialization",    source = "specialization")
    @Mapping(target = "lawFirm",           source = "lawFirm")
    @Mapping(target = "verificationStatus",source = "verificationStatus")
    @Mapping(target = "yearsOfPractice",   source = "yearsOfPractice")
    AdvocateReportResponse toAdvocateReport(Advocate advocate);

    List<AdvocateReportResponse> toAdvocateReportList(List<Advocate> advocates);

    // ── Clerk ─────────────────────────────────────────────────────────────

    @Mapping(target = "clerkUuid",     source = "uuid")
    @Mapping(target = "clerkName",     source = "user.username")
    @Mapping(target = "employeeId",    source = "employeeId")
    @Mapping(target = "courtName",     source = "court.courtName")
    @Mapping(target = "courtSection",  source = "courtSection")
    @Mapping(target = "department",    source = "department")
    ClerkReportResponse toClerkReport(Clerk clerk);

    List<ClerkReportResponse> toClerkReportList(List<Clerk> clerks);
}
