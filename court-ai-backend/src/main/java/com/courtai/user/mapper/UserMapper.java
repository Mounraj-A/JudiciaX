package com.courtai.user.mapper;

import com.courtai.user.dto.UserResponse;
import com.courtai.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between {@link User} entity and DTOs.
 *
 * <p>Spring component model is set globally via {@code -Amapstruct.defaultComponentModel=spring}
 * in the Maven compiler plugin, making all mappers injectable as Spring beans.</p>
 *
 * <p>Unmapped fields are handled with {@link ReportingPolicy#IGNORE} to prevent
 * compile errors for fields not present in the target (e.g., passwordHash → DTO).</p>
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Maps a {@link User} entity to a {@link UserResponse} DTO.
     * The {@code passwordHash} field is automatically excluded as it's not in the target.
     */
    @Mapping(source = "uuid", target = "uuid")
    @Mapping(source = "role", target = "role")
    UserResponse toResponse(User user);

    /**
     * Maps a list of {@link User} entities to a list of {@link UserResponse} DTOs.
     */
    List<UserResponse> toResponseList(List<User> users);
}
