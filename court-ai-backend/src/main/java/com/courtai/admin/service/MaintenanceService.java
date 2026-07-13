package com.courtai.admin.service;

import com.courtai.admin.dto.MaintenanceRequest;
import com.courtai.admin.dto.MaintenanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Maintenance window management service. */
public interface MaintenanceService {
    Page<MaintenanceResponse> getAll(Pageable pageable);
    MaintenanceResponse getByUuid(String uuid);
    MaintenanceResponse create(MaintenanceRequest request, String adminUuid);
    MaintenanceResponse update(String uuid, MaintenanceRequest request, String adminUuid);
    void delete(String uuid, String adminUuid);
    MaintenanceResponse activate(String uuid, String adminUuid);
    MaintenanceResponse complete(String uuid, String adminUuid);
    MaintenanceResponse cancel(String uuid, String adminUuid);
}
