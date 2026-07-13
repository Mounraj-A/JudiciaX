package com.courtai.judge.mapper;

import com.courtai.judge.dto.JudgeOrderResponse;
import com.courtai.judge.entity.JudgeOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for JudgeOrder → JudgeOrderResponse.
 */
@Mapper(componentModel = "spring")
public interface JudgeOrderMapper {

    @Mapping(target = "uuid",             source = "uuid")
    @Mapping(target = "caseUuid",         source = "caseFile.uuid")
    @Mapping(target = "caseNumber",       source = "caseFile.caseNumber")
    @Mapping(target = "orderType",        source = "orderType")
    @Mapping(target = "title",            source = "title")
    @Mapping(target = "orderText",        source = "orderText")
    @Mapping(target = "orderDate",        source = "orderDate")
    @Mapping(target = "originalFileName", source = "originalFileName")
    @Mapping(target = "mimeType",         source = "mimeType")
    @Mapping(target = "storagePath",      source = "storagePath")
    @Mapping(target = "fileSizeBytes",    source = "fileSizeBytes")
    @Mapping(target = "isSigned",         source = "isSigned")
    @Mapping(target = "remarks",          source = "remarks")
    @Mapping(target = "createdAt",        source = "createdAt")
    @Mapping(target = "updatedAt",        source = "updatedAt")
    JudgeOrderResponse toResponse(JudgeOrder order);
}
