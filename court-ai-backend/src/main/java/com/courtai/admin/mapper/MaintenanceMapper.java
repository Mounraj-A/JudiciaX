package com.courtai.admin.mapper;

import com.courtai.admin.dto.MaintenanceResponse;
import com.courtai.admin.entity.MaintenanceWindow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {

    @Mapping(target = "uuid",        source = "uuid")
    @Mapping(target = "title",       source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "startTime",   source = "startTime")
    @Mapping(target = "endTime",     source = "endTime")
    @Mapping(target = "status",      source = "status")
    @Mapping(target = "createdAt",   source = "createdAt")
    @Mapping(target = "updatedAt",   source = "updatedAt")
    MaintenanceResponse toResponse(MaintenanceWindow window);
}
