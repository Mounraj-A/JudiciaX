package com.courtai.advocate.mapper;

import com.courtai.advocate.dto.DocumentResponse;
import com.courtai.document.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Document} → {@link DocumentResponse}.
 */
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "uuid",             source = "uuid")
    @Mapping(target = "originalFileName", source = "originalFileName")
    @Mapping(target = "documentType",     source = "documentType")
    @Mapping(target = "mimeType",         source = "mimeType")
    @Mapping(target = "fileSizeBytes",    source = "fileSizeBytes")
    @Mapping(target = "description",      source = "description")
    @Mapping(target = "version",          source = "version")
    @Mapping(target = "isVerified",       source = "isVerified")
    @Mapping(target = "isConfidential",   source = "isConfidential")
    @Mapping(target = "uploadedByUuid",   source = "uploadedByUuid")
    @Mapping(target = "createdAt",        source = "createdAt")
    DocumentResponse toDocumentResponse(Document document);
}
