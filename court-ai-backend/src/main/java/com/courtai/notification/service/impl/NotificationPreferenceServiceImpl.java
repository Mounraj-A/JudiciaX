package com.courtai.notification.service.impl;

import com.courtai.common.enums.NotificationType;
import com.courtai.exception.ResourceNotFoundException;
import com.courtai.notification.dto.NotificationPreferenceRequest;
import com.courtai.notification.dto.NotificationPreferenceResponse;
import com.courtai.notification.entity.NotificationPreference;
import com.courtai.notification.mapper.NotificationPreferenceMapper;
import com.courtai.notification.repository.NotificationPreferenceRepository;
import com.courtai.notification.service.NotificationPreferenceService;
import com.courtai.user.entity.User;
import com.courtai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository repository;
    private final NotificationPreferenceMapper mapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationPreferenceResponse updatePreference(String userUuid, NotificationPreferenceRequest request) {
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "uuid", userUuid));

        Optional<NotificationPreference> existing = repository.findByUserUuidAndChannelAndNotificationTypeAndIsDeletedFalse(
                userUuid, request.getChannel(), request.getNotificationType());

        NotificationPreference preference;
        if (existing.isPresent()) {
            preference = existing.get();
            mapper.updateEntityFromRequest(request, preference);
        } else {
            preference = mapper.toEntity(request);
            preference.setUuid(UUID.randomUUID().toString());
            preference.setUser(user);
        }
        return mapper.toResponse(repository.save(preference));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreferenceResponse> getUserPreferences(String userUuid) {
        return mapper.toResponseList(repository.findByUserUuidAndIsDeletedFalse(userUuid));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDeliveryEnabled(String userUuid, NotificationType channel, String notificationType) {
        Optional<NotificationPreference> specific = repository.findByUserUuidAndChannelAndNotificationTypeAndIsDeletedFalse(
                userUuid, channel, notificationType);
        if (specific.isPresent()) {
            return checkQuietHours(specific.get());
        }
        // Fallback to ALL
        Optional<NotificationPreference> all = repository.findByUserUuidAndChannelAndNotificationTypeAndIsDeletedFalse(
                userUuid, channel, "ALL");
        if (all.isPresent()) {
            return checkQuietHours(all.get());
        }
        return true; // Default enabled
    }

    private boolean checkQuietHours(NotificationPreference pref) {
        if (!pref.getIsEnabled()) return false;
        if (pref.getQuietHoursStart() == null || pref.getQuietHoursEnd() == null) return true;
        
        LocalTime now = LocalTime.now();
        LocalTime start = pref.getQuietHoursStart();
        LocalTime end = pref.getQuietHoursEnd();

        if (start.isBefore(end)) {
            // Quiet hours e.g., 14:00 to 16:00
            if (now.isAfter(start) && now.isBefore(end)) return false;
        } else {
            // Quiet hours cross midnight e.g., 22:00 to 07:00
            if (now.isAfter(start) || now.isBefore(end)) return false;
        }
        return true;
    }
}