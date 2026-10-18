package com.tracker.toilet.service;

import com.tracker.toilet.dto.CleaningLogResponse;
import com.tracker.toilet.dto.CreateCleaningLogRequest;
import com.tracker.toilet.exception.BadRequestException;
import com.tracker.toilet.model.CleaningLog;
import com.tracker.toilet.model.ToiletFacility;
import com.tracker.toilet.repository.CleaningLogRepository;
import com.tracker.toilet.repository.ToiletFacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CleaningLogService {

    private final CleaningLogRepository cleaningLogRepository;
    private final FacilityService facilityService;
    private final ToiletFacilityRepository facilityRepository;

    public CleaningLogService(CleaningLogRepository cleaningLogRepository,
                              FacilityService facilityService,
                              ToiletFacilityRepository facilityRepository) {
        this.cleaningLogRepository = cleaningLogRepository;
        this.facilityService = facilityService;
        this.facilityRepository = facilityRepository;
    }

    public CleaningLogResponse createCleaningLog(CreateCleaningLogRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null.");
        }
        if (request.getFacilityId() == null) {
            throw new BadRequestException("Facility ID is required to log a cleaning event.");
        }
        if (request.getCleanerName() == null || request.getCleanerName().trim().isEmpty()) {
            throw new BadRequestException("Cleaner name is required.");
        }
        if (request.getHygieneRating() < 1 || request.getHygieneRating() > 5) {
            throw new BadRequestException("Hygiene rating must be between 1 (poor) and 5 (excellent).");
        }

        ToiletFacility facility = facilityService.getFacilityEntity(request.getFacilityId());

        CleaningLog log = new CleaningLog(
                facility,
                request.getCleanerName().trim(),
                request.isWaterSupplied(),
                request.isSoapAvailable(),
                request.getHygieneRating(),
                request.getRemarks()
        );

        // Update facility operational indicators based on field inspection
        facility.setHasRunningWater(request.isWaterSupplied());
        facility.setCleanlinessScore(request.getHygieneRating() * 20.0);
        if (request.getHygieneRating() < 3 || !request.isWaterSupplied()) {
            facility.setOperationalStatus("MAINTENANCE_REQUIRED");
        } else {
            facility.setOperationalStatus("OPERATIONAL");
        }
        facilityRepository.save(facility);

        CleaningLog saved = cleaningLogRepository.save(log);
        return CleaningLogResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<CleaningLogResponse> getAllCleaningLogs(Long facilityId) {
        List<CleaningLog> logs;
        if (facilityId != null) {
            logs = cleaningLogRepository.findByFacilityId(facilityId);
        } else {
            logs = cleaningLogRepository.findAll();
        }
        return logs.stream().map(CleaningLogResponse::fromEntity).collect(Collectors.toList());
    }
}
