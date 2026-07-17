package com.courtai.notification.mapper;

import com.courtai.notification.dto.NotificationEventResponse;
import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSummaryResponse;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.entity.NotificationEventLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(source = "uuid", target = "uuid")
    NotificationResponse toResponse(Notification entity);

    List<NotificationResponse> toResponseList(List<Notification> entities);

    @Mapping(source = "uuid", target = "uuid")
    NotificationSummaryResponse toSummaryResponse(Notification entity);

    List<NotificationSummaryResponse> toSummaryResponseList(List<Notification> entities);

    @Mapping(source = "uuid", target = "uuid")
    NotificationEventResponse toEventResponse(NotificationEventLog entity);

    List<NotificationEventResponse> toEventResponseList(List<NotificationEventLog> entities);
}