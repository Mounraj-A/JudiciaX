package com.courtai.master.mapper;

import com.courtai.casecategory.entity.CaseCategory;
import com.courtai.master.dto.CaseCategoryDto;
import com.courtai.master.dto.CaseTypeDto;
import com.courtai.master.entity.CaseType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MasterCaseDataMapper {

    CaseTypeDto toCaseTypeDto(CaseType caseType);

    @Mapping(target = "caseTypeUuid", source = "caseType.uuid")
    @Mapping(target = "caseTypeCode", source = "caseType.typeCode")
    CaseCategoryDto toCaseCategoryDto(CaseCategory caseCategory);
}
