package com.courtai.judge.mapper;

import com.courtai.hearing.entity.Hearing;
import com.courtai.judge.dto.JudgeHearingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Hearing → JudgeHearingResponse.
 */
@Mapper(componentModel = "spring")
public interface JudgeHearingMapper {

    @Mapping(target = "uuid",             source = "uuid")
    @Mapping(target = "caseUuid",         source = "caseFile.uuid")
    @Mapping(target = "caseNumber",       source = "caseFile.caseNumber")
    @Mapping(target = "caseTitle",        source = "caseFile.caseTitle")
    @Mapping(target = "scheduledAt",      source = "scheduledAt")
    @Mapping(target = "actualStartAt",    source = "actualStartAt")
    @Mapping(target = "actualEndAt",      source = "actualEndAt")
    @Mapping(target = "status",           source = "status")
    @Mapping(target = "adjournReason",    source = "adjournReason")
    @Mapping(target = "nextHearingDate",  source = "nextHearingDate")
    @Mapping(target = "hearingNumber",    source = "hearingNumber")
    @Mapping(target = "notes",            source = "notes")
    @Mapping(target = "isVirtual",        source = "isVirtual")
    @Mapping(target = "courtRoomUuid",    source = "courtRoom.uuid")
    @Mapping(target = "courtRoomNumber",  source = "courtRoom.roomNumber")
    @Mapping(target = "createdAt",        source = "createdAt")
    @Mapping(target = "updatedAt",        source = "updatedAt")
    JudgeHearingResponse toResponse(Hearing hearing);
}
