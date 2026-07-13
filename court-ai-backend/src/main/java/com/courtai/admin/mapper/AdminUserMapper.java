package com.courtai.admin.mapper;

import com.courtai.admin.dto.UserSummaryResponse;
import com.courtai.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper for User → admin DTOs. */
@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    @Mapping(target = "uuid",            source = "uuid")
    @Mapping(target = "username",        source = "username")
    @Mapping(target = "fullName",        expression = "java(user.getDisplayName())")
    @Mapping(target = "email",           source = "email")
    @Mapping(target = "phoneNumber",     source = "phoneNumber")
    @Mapping(target = "role",            source = "role")
    @Mapping(target = "accountStatus",   source = "accountStatus")
    @Mapping(target = "isEmailVerified", source = "isEmailVerified")
    @Mapping(target = "isLocked",        source = "isLocked")
    @Mapping(target = "lastLogin",       source = "lastLogin")
    @Mapping(target = "createdAt",       source = "createdAt")
    UserSummaryResponse toSummary(User user);
}
