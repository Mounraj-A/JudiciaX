package com.courtai.admin.mapper;

import com.courtai.admin.dto.AnnouncementResponse;
import com.courtai.admin.entity.SystemAnnouncement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {

    @Mapping(target = "uuid",       source = "uuid")
    @Mapping(target = "title",      source = "title")
    @Mapping(target = "message",    source = "message")
    @Mapping(target = "priority",   source = "priority")
    @Mapping(target = "targetRole", source = "targetRole")
    @Mapping(target = "startDate",  source = "startDate")
    @Mapping(target = "endDate",    source = "endDate")
    @Mapping(target = "isActive",   source = "isActive")
    @Mapping(target = "createdAt",  source = "createdAt")
    @Mapping(target = "updatedAt",  source = "updatedAt")
    AnnouncementResponse toResponse(SystemAnnouncement announcement);
}
