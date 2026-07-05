package com.courtai.user.service;

import com.courtai.user.dto.CreateUserRequest;
import com.courtai.user.dto.UserResponse;

import java.util.List;

/**
 * Service contract for user management operations.
 *
 * <p>Business logic will be implemented in {@link UserServiceImpl}.
 * Controllers must depend on this interface, not the implementation.</p>
 */
public interface UserService {

    /**
     * Creates a new user account.
     *
     * @param request the validated creation request DTO
     * @return the created user as a response DTO
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Retrieves a user by their public UUID.
     *
     * @param uuid the UUID of the user
     * @return the user response DTO
     */
    UserResponse getUserByUuid(String uuid);

    /**
     * Retrieves all active users in the system.
     *
     * @return list of user response DTOs
     */
    List<UserResponse> getAllUsers();

    /**
     * Soft-deletes a user account by UUID.
     *
     * @param uuid the UUID of the user to delete
     */
    void deleteUser(String uuid);
}
