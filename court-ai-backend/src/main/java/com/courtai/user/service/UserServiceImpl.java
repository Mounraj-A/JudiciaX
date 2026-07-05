package com.courtai.user.service;

import com.courtai.exception.DuplicateResourceException;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.user.dto.CreateUserRequest;
import com.courtai.user.dto.UserResponse;
import com.courtai.user.entity.User;
import com.courtai.user.mapper.UserMapper;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link UserService}.
 *
 * <p>Follows SOLID principles:</p>
 * <ul>
 *   <li>Constructor injection only (no field injection)</li>
 *   <li>Delegates mapping to {@link UserMapper}</li>
 *   <li>Delegates persistence to {@link UserRepository}</li>
 *   <li>No business logic in controllers</li>
 * </ul>
 *
 * <p>NOTE: Business logic for AI prioritization and role-specific workflows
 * will be added in future implementation phases.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: [{}] and role: [{}]", request.getEmail(), request.getRole());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .isActive(true)
                .isEmailVerified(false)
                .isLocked(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with UUID: [{}]", savedUser.getUuid());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserByUuid(String uuid) {
        log.debug("Fetching user by UUID: [{}]", uuid);
        User user = userRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", uuid));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all active users");
        return userMapper.toResponseList(userRepository.findAllByIsDeletedFalse());
    }

    @Override
    @Transactional
    public void deleteUser(String uuid) {
        log.info("Soft-deleting user with UUID: [{}]", uuid);
        User user = userRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", uuid));
        user.softDelete();
        userRepository.save(user);
        log.info("User soft-deleted: [{}]", uuid);
    }
}
