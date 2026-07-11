package com.courtai.advocate.mapper;

import com.courtai.advocate.dto.HearingResponse;
import com.courtai.hearing.entity.Hearing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Hearing} → {@link HearingResponse}.
 */
@Mapper(componentModel = "spring")
public interface HearingMapper {

    @Mapping(target = "uuid",            source = "uuid")
    @Mapping(target = "caseUuid",        source = "caseFile.uuid")
    @Mapping(target = "caseNumber",      source = "caseFile.caseNumber")
    @Mapping(target = "caseTitle",       source = "caseFile.caseTitle")
    @Mapping(target = "courtName",       source = "caseFile.court.courtName")
    @Mapping(target = "courtRoomName",   source = "courtRoom.roomNumber")
    @Mapping(target = "judgeName",       source = "judge.user.fullName")
    @Mapping(target = "scheduledAt",     source = "scheduledAt")
    @Mapping(target = "status",          source = "status")
    @Mapping(target = "hearingNumber",   source = "hearingNumber")
    @Mapping(target = "isVirtual",       source = "isVirtual")
    @Mapping(target = "adjournReason",   source = "adjournReason")
    @Mapping(target = "nextHearingDate", source = "nextHearingDate")
    @Mapping(target = "notes",           source = "notes")
    @Mapping(target = "createdAt",       source = "createdAt")
    HearingResponse toHearingResponse(Hearing hearing);
}
