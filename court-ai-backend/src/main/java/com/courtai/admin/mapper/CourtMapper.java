package com.courtai.admin.mapper;

import com.courtai.admin.dto.CourtBenchResponse;
import com.courtai.admin.dto.CourtResponse;
import com.courtai.admin.dto.CourtRoomResponse;
import com.courtai.court.entity.Court;
import com.courtai.court.entity.CourtBench;
import com.courtai.court.entity.CourtRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper for Court entities → admin DTOs. */
@Mapper(componentModel = "spring")
public interface CourtMapper {

    @Mapping(target = "uuid",        source = "uuid")
    @Mapping(target = "courtCode",   source = "courtCode")
    @Mapping(target = "courtName",   source = "courtName")
    @Mapping(target = "courtType",   source = "courtType")
    @Mapping(target = "state",       source = "state")
    @Mapping(target = "district",    source = "district")
    @Mapping(target = "address",     source = "address")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email",       source = "email")
    @Mapping(target = "isActive",    source = "isActive")
    @Mapping(target = "createdAt",   source = "createdAt")
    @Mapping(target = "updatedAt",   source = "updatedAt")
    CourtResponse toResponse(Court court);

    @Mapping(target = "uuid",         source = "uuid")
    @Mapping(target = "courtUuid",    source = "court.uuid")
    @Mapping(target = "courtName",    source = "court.courtName")
    @Mapping(target = "benchNumber",  source = "benchNumber")
    @Mapping(target = "benchType",    source = "benchType")
    @Mapping(target = "description",  source = "description")
    @Mapping(target = "isActive",     source = "isActive")
    @Mapping(target = "createdAt",    source = "createdAt")
    CourtBenchResponse toResponse(CourtBench bench);

    @Mapping(target = "uuid",                 source = "uuid")
    @Mapping(target = "courtUuid",            source = "court.uuid")
    @Mapping(target = "courtName",            source = "court.courtName")
    @Mapping(target = "roomNumber",           source = "roomNumber")
    @Mapping(target = "floor",                source = "floor")
    @Mapping(target = "capacity",             source = "capacity")
    @Mapping(target = "hasVideoConferencing", source = "hasVideoConferencing")
    @Mapping(target = "isActive",             source = "isActive")
    @Mapping(target = "createdAt",            source = "createdAt")
    CourtRoomResponse toResponse(CourtRoom room);
}
