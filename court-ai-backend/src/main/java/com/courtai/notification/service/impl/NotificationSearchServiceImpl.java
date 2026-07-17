package com.courtai.notification.service.impl;

import com.courtai.notification.dto.NotificationResponse;
import com.courtai.notification.dto.NotificationSearchRequest;
import com.courtai.notification.entity.Notification;
import com.courtai.notification.mapper.NotificationMapper;
import com.courtai.notification.repository.NotificationRepository;
import com.courtai.notification.service.NotificationSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSearchServiceImpl implements NotificationSearchService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> search(String userUuid, NotificationSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(), 
                request.getSize(), 
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );

        Specification<Notification> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("recipient").get("uuid"), userUuid));
            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String likePattern = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(root.get("message")), likePattern)
                ));
            }
            if (request.getIsRead() != null) {
                predicates.add(cb.equal(root.get("isRead"), request.getIsRead()));
            }
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Notification> entityPage = notificationRepository.findAll(spec, pageable);
        return entityPage.map(notificationMapper::toResponse);
    }
}