package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationDeliveryResponse;
import com.courtai.notification.entity.NotificationDelivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationDeliveryMapper {

    @Mapping(source = "uuid", target = "uuid")
    NotificationDeliveryResponse toResponse(NotificationDelivery entity);

    List<NotificationDeliveryResponse> toResponseList(List<NotificationDelivery> entities);
}