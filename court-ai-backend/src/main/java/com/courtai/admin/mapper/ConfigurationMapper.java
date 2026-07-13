package com.courtai.admin.mapper;

import com.courtai.admin.dto.ConfigurationResponse;
import com.courtai.admin.entity.SystemConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConfigurationMapper {

    @Mapping(target = "uuid",          source = "uuid")
    @Mapping(target = "configKey",     source = "configKey")
    @Mapping(target = "configValue",   source = "configValue")
    @Mapping(target = "description",   source = "description")
    @Mapping(target = "category",      source = "category")
    @Mapping(target = "isEditable",    source = "isEditable")
    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt")
    @Mapping(target = "createdAt",     source = "createdAt")
    @Mapping(target = "updatedAt",     source = "updatedAt")
    ConfigurationResponse toResponse(SystemConfiguration config);
}
