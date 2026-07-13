package com.courtai.admin.service;

import com.courtai.admin.dto.AnnouncementRequest;
import com.courtai.admin.dto.AnnouncementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Admin announcement management service. */
public interface AnnouncementService {
    Page<AnnouncementResponse> getAll(Pageable pageable);
    AnnouncementResponse getByUuid(String uuid);
    AnnouncementResponse create(AnnouncementRequest request, String adminUuid);
    AnnouncementResponse update(String uuid, AnnouncementRequest request, String adminUuid);
    void delete(String uuid, String adminUuid);
    AnnouncementResponse publish(String uuid, String adminUuid);
    AnnouncementResponse archive(String uuid, String adminUuid);
}
