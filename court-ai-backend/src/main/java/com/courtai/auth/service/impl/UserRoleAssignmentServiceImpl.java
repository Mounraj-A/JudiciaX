package com.courtai.auth.service.impl;

import com.courtai.audit.service.AuditService;
import com.courtai.auth.dto.AssignRoleRequest;
import com.courtai.auth.service.UserRoleAssignmentService;
import com.courtai.common.enums.UserRole;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.exception.UnauthorizedActionException;
import com.courtai.notification.service.NotificationService;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link UserRoleAssignmentService}.
 *
 * <p>Manages role changes on User entities, ensuring ADMIN role cannot be
 * removed via this API (must go through admin-specific flows).
 * Emits audit events and in-app notifications on every change.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleAssignmentServiceImpl implements UserRoleAssignmentService {

    private final UserRepository      userRepository;
    private final AuditService        auditService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public User assignRoleToUser(AssignRoleRequest request) {
        log.info("Assigning role {} to user {}", request.role(), request.userUuid());

        User user = userRepository.findByUuidAndIsDeletedFalse(request.userUuid())
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", request.userUuid()));

        UserRole previousRole = user.getRole();
        user.setRole(request.role());
        User saved = userRepository.save(user);

        log.info("Role changed from {} to {} for user {}", previousRole, request.role(), request.userUuid());

        auditService.logSuccess(
                "ROLE_ASSIGNED", "User", request.userUuid(),
                String.format("Role changed from %s to %s", previousRole, request.role()));

        notificationService.sendInAppNotification(
                request.userUuid(),
                "Role Updated",
                String.format("Your role has been updated to '%s'. Please re-login to apply changes.",
                        request.role().name()),
                request.userUuid(),
                "User");

        return saved;
    }

    @Override
    @Transactional
    public User removeRoleFromUser(String userUuid) {
        log.info("Removing role from user {}", userUuid);

        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

        if (user.getRole() == UserRole.ROLE_ADMIN) {
            throw new UnauthorizedActionException(
                    "Cannot remove ROLE_ADMIN via this endpoint. Use the admin management API.");
        }

        UserRole previousRole = user.getRole();
        user.setRole(UserRole.ROLE_ADVOCATE); // Default fallback role
        User saved = userRepository.save(user);

        log.info("Role {} removed from user {} — reset to ROLE_ADVOCATE", previousRole, userUuid);

        auditService.logSuccess(
                "ROLE_REMOVED", "User", userUuid,
                String.format("Role %s removed — reset to ROLE_ADVOCATE", previousRole));

        notificationService.sendInAppNotification(
                userUuid,
                "Role Removed",
                String.format("Your role '%s' has been removed. Your access has been updated.", previousRole.name()),
                userUuid,
                "User");

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRoleAndIsDeletedFalse(role);
    }
}
