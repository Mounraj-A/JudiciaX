package com.courtai.advocate.mapper;

import com.courtai.advocate.dto.AdvocateProfileResponse;
import com.courtai.advocate.entity.Advocate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between {@link Advocate} entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface AdvocateMapper {

    @Mapping(target = "uuid",        source = "uuid")
    @Mapping(target = "fullName",    source = "user.fullName")
    @Mapping(target = "email",       source = "user.email")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    @Mapping(target = "barCouncilNumber",    source = "barCouncilNumber")
    @Mapping(target = "enrollmentDate",      source = "enrollmentDate")
    @Mapping(target = "stateBarCouncil",     source = "stateBarCouncil")
    @Mapping(target = "lawFirm",             source = "lawFirm")
    @Mapping(target = "specialization",      source = "specialization")
    @Mapping(target = "yearsOfPractice",     source = "yearsOfPractice")
    @Mapping(target = "officeAddress",       source = "officeAddress")
    @Mapping(target = "officeCity",          source = "officeCity")
    @Mapping(target = "officeState",         source = "officeState")
    @Mapping(target = "officePincode",       source = "officePincode")
    @Mapping(target = "profilePhotoPath",    source = "profilePhotoPath")
    @Mapping(target = "digitalSignaturePath",source = "digitalSignaturePath")
    @Mapping(target = "verificationStatus",  source = "verificationStatus")
    @Mapping(target = "verifiedAt",          source = "verifiedAt")
    @Mapping(target = "createdAt",           source = "createdAt")
    @Mapping(target = "updatedAt",           source = "updatedAt")
    AdvocateProfileResponse toProfileResponse(Advocate advocate);
}
