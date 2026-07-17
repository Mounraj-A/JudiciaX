package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationTemplateRequest;
import com.courtai.notification.dto.NotificationTemplateResponse;
import com.courtai.notification.entity.NotificationTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationTemplateMapper {

    NotificationTemplate toEntity(NotificationTemplateRequest request);

    @Mapping(source = "uuid", target = "uuid")
    NotificationTemplateResponse toResponse(NotificationTemplate entity);

    List<NotificationTemplateResponse> toResponseList(List<NotificationTemplate> entities);

    void updateEntityFromRequest(NotificationTemplateRequest request, @MappingTarget NotificationTemplate entity);
}