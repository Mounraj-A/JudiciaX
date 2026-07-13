package com.courtai.admin.service.impl;

import com.courtai.admin.dto.*;
import com.courtai.admin.mapper.CourtMapper;
import com.courtai.admin.service.CourtManagementService;
import com.courtai.audit.service.AuditService;
import com.courtai.court.entity.Court;
import com.courtai.court.entity.CourtBench;
import com.courtai.court.entity.CourtRoom;
import com.courtai.court.repository.CourtBenchRepository;
import com.courtai.court.repository.CourtRepository;
import com.courtai.court.repository.CourtRoomRepository;
import com.courtai.exception.BusinessRuleViolationException;
import com.courtai.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourtManagementServiceImpl implements CourtManagementService {

    private final CourtRepository      courtRepository;
    private final CourtBenchRepository benchRepository;
    private final CourtRoomRepository  roomRepository;
    private final CourtMapper          courtMapper;
    private final AuditService         auditService;

    // ── Court ─────────────────────────────────────────────────────────────────

    @Override
    public Page<CourtResponse> getCourts(Pageable pageable) {
        return courtRepository.findAll(pageable).map(courtMapper::toResponse);
    }

    @Override
    public CourtResponse getCourtByUuid(String uuid) {
        return courtMapper.toResponse(loadCourt(uuid));
    }

    @Override
    @Transactional
    public CourtResponse createCourt(CourtRequest req, String adminUuid) {
        if (courtRepository.existsByCourtCode(req.getCourtCode())) {
            throw new BusinessRuleViolationException(
                    "Court with code '" + req.getCourtCode() + "' already exists.");
        }
        Court court = Court.builder()
                .courtCode(req.getCourtCode())
                .courtName(req.getCourtName())
                .courtType(req.getCourtType())
                .state(req.getState())
                .district(req.getDistrict())
                .address(req.getAddress())
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .isActive(req.getIsActive() != null ? req.getIsActive() : Boolean.TRUE)
                .build();
        courtRepository.save(court);
        auditService.logSuccess("COURT_CREATED", "Court", court.getUuid(),
                "Court '" + court.getCourtCode() + "' created by admin " + adminUuid);
        return courtMapper.toResponse(court);
    }

    @Override
    @Transactional
    public CourtResponse updateCourt(String uuid, CourtRequest req, String adminUuid) {
        Court court = loadCourt(uuid);
        if (req.getCourtCode() != null && !req.getCourtCode().equals(court.getCourtCode())
                && courtRepository.existsByCourtCode(req.getCourtCode())) {
            throw new BusinessRuleViolationException(
                    "Another court with code '" + req.getCourtCode() + "' already exists.");
        }
        if (req.getCourtCode()   != null) court.setCourtCode(req.getCourtCode());
        if (req.getCourtName()   != null) court.setCourtName(req.getCourtName());
        if (req.getCourtType()   != null) court.setCourtType(req.getCourtType());
        if (req.getState()       != null) court.setState(req.getState());
        if (req.getDistrict()    != null) court.setDistrict(req.getDistrict());
        if (req.getAddress()     != null) court.setAddress(req.getAddress());
        if (req.getPhoneNumber() != null) court.setPhoneNumber(req.getPhoneNumber());
        if (req.getEmail()       != null) court.setEmail(req.getEmail());
        if (req.getIsActive()    != null) court.setIsActive(req.getIsActive());
        courtRepository.save(court);
        auditService.logSuccess("COURT_UPDATED", "Court", uuid,
                "Court updated by admin " + adminUuid);
        return courtMapper.toResponse(court);
    }

    @Override
    @Transactional
    public void deleteCourt(String uuid, String adminUuid) {
        Court court = loadCourt(uuid);
        if (Boolean.TRUE.equals(court.getIsActive())) {
            throw new BusinessRuleViolationException("Cannot delete an active court. Deactivate it first.");
        }
        court.softDelete();
        courtRepository.save(court);
        auditService.logSuccess("COURT_DELETED", "Court", uuid,
                "Court soft-deleted by admin " + adminUuid);
    }

    // ── Bench ─────────────────────────────────────────────────────────────────

    @Override
    public List<CourtBenchResponse> getBenchesByCourt(String courtUuid) {
        Court court = loadCourt(courtUuid);
        return benchRepository.findByCourtIdAndIsDeletedFalse(court.getId())
                .stream().map(courtMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourtBenchResponse createBench(CourtBenchRequest req, String adminUuid) {
        Court court = loadCourt(req.getCourtUuid());
        CourtBench bench = CourtBench.builder()
                .court(court)
                .benchNumber(req.getBenchNumber())
                .benchType(req.getBenchType())
                .description(req.getDescription())
                .isActive(req.getIsActive() != null ? req.getIsActive() : Boolean.TRUE)
                .build();
        benchRepository.save(bench);
        auditService.logSuccess("BENCH_CREATED", "CourtBench", bench.getUuid(),
                "Bench created by admin " + adminUuid);
        return courtMapper.toResponse(bench);
    }

    @Override
    @Transactional
    public CourtBenchResponse updateBench(String benchUuid, CourtBenchRequest req, String adminUuid) {
        CourtBench bench = benchRepository.findByUuidAndIsDeletedFalse(benchUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CourtBench", "uuid", benchUuid));
        if (req.getBenchNumber() != null) bench.setBenchNumber(req.getBenchNumber());
        if (req.getBenchType()   != null) bench.setBenchType(req.getBenchType());
        if (req.getDescription() != null) bench.setDescription(req.getDescription());
        if (req.getIsActive()    != null) bench.setIsActive(req.getIsActive());
        benchRepository.save(bench);
        auditService.logSuccess("BENCH_UPDATED", "CourtBench", benchUuid,
                "Bench updated by admin " + adminUuid);
        return courtMapper.toResponse(bench);
    }

    @Override
    @Transactional
    public void deleteBench(String benchUuid, String adminUuid) {
        CourtBench bench = benchRepository.findByUuidAndIsDeletedFalse(benchUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CourtBench", "uuid", benchUuid));
        bench.softDelete();
        benchRepository.save(bench);
        auditService.logSuccess("BENCH_DELETED", "CourtBench", benchUuid,
                "Bench soft-deleted by admin " + adminUuid);
    }

    // ── CourtRoom ─────────────────────────────────────────────────────────────

    @Override
    public List<CourtRoomResponse> getRoomsByCourt(String courtUuid) {
        Court court = loadCourt(courtUuid);
        return roomRepository.findByCourtIdAndIsDeletedFalse(court.getId())
                .stream().map(courtMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourtRoomResponse createRoom(CourtRoomRequest req, String adminUuid) {
        Court court = loadCourt(req.getCourtUuid());
        CourtRoom room = CourtRoom.builder()
                .court(court)
                .roomNumber(req.getRoomNumber())
                .floor(req.getFloor())
                .capacity(req.getCapacity())
                .hasVideoConferencing(req.getHasVideoConferencing() != null
                        ? req.getHasVideoConferencing() : Boolean.FALSE)
                .isActive(req.getIsActive() != null ? req.getIsActive() : Boolean.TRUE)
                .build();
        roomRepository.save(room);
        auditService.logSuccess("ROOM_CREATED", "CourtRoom", room.getUuid(),
                "Room created by admin " + adminUuid);
        return courtMapper.toResponse(room);
    }

    @Override
    @Transactional
    public CourtRoomResponse updateRoom(String roomUuid, CourtRoomRequest req, String adminUuid) {
        CourtRoom room = roomRepository.findByUuidAndIsDeletedFalse(roomUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CourtRoom", "uuid", roomUuid));
        if (req.getRoomNumber()          != null) room.setRoomNumber(req.getRoomNumber());
        if (req.getFloor()               != null) room.setFloor(req.getFloor());
        if (req.getCapacity()            != null) room.setCapacity(req.getCapacity());
        if (req.getHasVideoConferencing()!= null) room.setHasVideoConferencing(req.getHasVideoConferencing());
        if (req.getIsActive()            != null) room.setIsActive(req.getIsActive());
        roomRepository.save(room);
        auditService.logSuccess("ROOM_UPDATED", "CourtRoom", roomUuid,
                "Room updated by admin " + adminUuid);
        return courtMapper.toResponse(room);
    }

    @Override
    @Transactional
    public void deleteRoom(String roomUuid, String adminUuid) {
        CourtRoom room = roomRepository.findByUuidAndIsDeletedFalse(roomUuid)
                .orElseThrow(() -> new ResourceNotFoundException("CourtRoom", "uuid", roomUuid));
        room.softDelete();
        roomRepository.save(room);
        auditService.logSuccess("ROOM_DELETED", "CourtRoom", roomUuid,
                "Room soft-deleted by admin " + adminUuid);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Court loadCourt(String uuid) {
        return courtRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Court", "uuid", uuid));
    }
}
