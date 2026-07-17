package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;
import com.courtai.notification.entity.NotificationPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationPreferenceMapper {

    NotificationPreference toEntity(NotificationPreferenceRequest request);

    @Mapping(source = "uuid", target = "uuid")
    NotificationPreferenceResponse toResponse(NotificationPreference entity);

    List<NotificationPreferenceResponse> toResponseList(List<NotificationPreference> entities);

    void updateEntityFromRequest(NotificationPreferenceRequest request, @MappingTarget NotificationPreference entity);
}