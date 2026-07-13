package com.courtai.admin.service;

import com.courtai.admin.dto.CourtBenchRequest;
import com.courtai.admin.dto.CourtBenchResponse;
import com.courtai.admin.dto.CourtRequest;
import com.courtai.admin.dto.CourtResponse;
import com.courtai.admin.dto.CourtRoomRequest;
import com.courtai.admin.dto.CourtRoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/** Admin service for full CRUD management of courts, benches, and courtrooms. */
public interface CourtManagementService {

    // ── Court ─────────────────────────────────────────────────────────────────
    Page<CourtResponse> getCourts(Pageable pageable);
    CourtResponse getCourtByUuid(String uuid);
    CourtResponse createCourt(CourtRequest request, String adminUuid);
    CourtResponse updateCourt(String uuid, CourtRequest request, String adminUuid);
    void deleteCourt(String uuid, String adminUuid);

    // ── Bench ─────────────────────────────────────────────────────────────────
    List<CourtBenchResponse> getBenchesByCourt(String courtUuid);
    CourtBenchResponse createBench(CourtBenchRequest request, String adminUuid);
    CourtBenchResponse updateBench(String benchUuid, CourtBenchRequest request, String adminUuid);
    void deleteBench(String benchUuid, String adminUuid);

    // ── CourtRoom ─────────────────────────────────────────────────────────────
    List<CourtRoomResponse> getRoomsByCourt(String courtUuid);
    CourtRoomResponse createRoom(CourtRoomRequest request, String adminUuid);
    CourtRoomResponse updateRoom(String roomUuid, CourtRoomRequest request, String adminUuid);
    void deleteRoom(String roomUuid, String adminUuid);
}
