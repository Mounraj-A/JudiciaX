package com.courtai.judge.mapper;

import com.courtai.judge.dto.JudgeNoteResponse;
import com.courtai.note.entity.JudgeNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for JudgeNote → JudgeNoteResponse.
 */
@Mapper(componentModel = "spring")
public interface JudgeNoteMapper {

    @Mapping(target = "uuid",           source = "uuid")
    @Mapping(target = "caseUuid",       source = "caseFile.uuid")
    @Mapping(target = "noteText",       source = "noteText")
    @Mapping(target = "noteType",       source = "noteType")
    @Mapping(target = "noteDate",       source = "noteDate")
    @Mapping(target = "isConfidential", source = "isConfidential")
    @Mapping(target = "createdAt",      source = "createdAt")
    @Mapping(target = "updatedAt",      source = "updatedAt")
    JudgeNoteResponse toResponse(JudgeNote note);
}
