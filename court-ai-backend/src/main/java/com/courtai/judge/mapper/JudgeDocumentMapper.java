package com.courtai.judge.mapper;

import com.courtai.document.entity.Document;
import com.courtai.judge.dto.JudgeDocumentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting Document → JudgeDocumentResponse.
 * Read-only — no reverse mapping.
 */
@Mapper(componentModel = "spring")
public interface JudgeDocumentMapper {

    @Mapping(target = "uuid",                source = "uuid")
    @Mapping(target = "fileName",            source = "fileName")
    @Mapping(target = "originalFileName",    source = "originalFileName")
    @Mapping(target = "documentType",        source = "documentType")
    @Mapping(target = "mimeType",            source = "mimeType")
    @Mapping(target = "fileSizeBytes",       source = "fileSizeBytes")
    @Mapping(target = "pageCount",           source = "pageCount")
    @Mapping(target = "description",         source = "description")
    @Mapping(target = "version",             source = "version")
    @Mapping(target = "ocrStatus",           source = "ocrStatus")
    @Mapping(target = "isVerified",          source = "isVerified")
    @Mapping(target = "verificationRemarks", source = "verificationRemarks")
    @Mapping(target = "isConfidential",      source = "isConfidential")
    @Mapping(target = "uploadedByUuid",      source = "uploadedByUuid")
    @Mapping(target = "createdAt",           source = "createdAt")
    @Mapping(target = "verifiedAt",          source = "verifiedAt")
    JudgeDocumentResponse toResponse(Document document);
}
