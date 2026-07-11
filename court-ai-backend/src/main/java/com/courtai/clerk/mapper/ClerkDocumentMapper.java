package com.courtai.clerk.mapper;

import com.courtai.clerk.dto.DocumentVerificationResponse;
import com.courtai.document.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper for Document → DocumentVerificationResponse. */
@Mapper(componentModel = "spring")
public interface ClerkDocumentMapper {

    @Mapping(target = "uuid",                source = "uuid")
    @Mapping(target = "originalFileName",    source = "originalFileName")
    @Mapping(target = "documentType",        source = "documentType")
    @Mapping(target = "mimeType",            source = "mimeType")
    @Mapping(target = "fileSizeBytes",       source = "fileSizeBytes")
    @Mapping(target = "description",         source = "description")
    @Mapping(target = "isVerified",          source = "isVerified")
    @Mapping(target = "verificationRemarks", source = "verificationRemarks")
    @Mapping(target = "verifiedByUuid",      source = "verifiedByUuid")
    @Mapping(target = "verifiedAt",          source = "verifiedAt")
    @Mapping(target = "rejectionReason",     source = "rejectionReason")
    @Mapping(target = "uploadedByUuid",      source = "uploadedByUuid")
    @Mapping(target = "createdAt",           source = "createdAt")
    @Mapping(target = "caseUuid",            source = "caseFile.uuid")
    @Mapping(target = "caseNumber",          source = "caseFile.caseNumber")
    DocumentVerificationResponse toDocumentVerificationResponse(Document document);
}
