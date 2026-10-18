package com.tracker.toilet.service;

import com.tracker.toilet.dto.CreateFacilityRequest;
import com.tracker.toilet.dto.FacilityResponse;
import com.tracker.toilet.exception.BadRequestException;
import com.tracker.toilet.exception.ResourceNotFoundException;
import com.tracker.toilet.model.ToiletFacility;
import com.tracker.toilet.repository.ToiletFacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacilityService {

    private final ToiletFacilityRepository facilityRepository;

    public FacilityService(ToiletFacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Transactional(readOnly = true)
    public List<FacilityResponse> getAllFacilities() {
        return getAllFacilities(null, null, null);
    }

    @Transactional(readOnly = true)
    public List<FacilityResponse> getAllFacilities(String search, String ward, String status) {
        return facilityRepository.findAll()
                .stream()
                .filter(facility -> {
                    if (search != null && !search.isBlank()) {
                        String term = search.trim().toLowerCase();
                        boolean matchName = facility.getName() != null && facility.getName().toLowerCase().contains(term);
                        boolean matchCode = facility.getFacilityCode() != null && facility.getFacilityCode().toLowerCase().contains(term);
                        boolean matchAddr = facility.getLocationAddress() != null && facility.getLocationAddress().toLowerCase().contains(term);
                        if (!matchName && !matchCode && !matchAddr) {
                            return false;
                        }
                    }
                    if (ward != null && !ward.isBlank()) {
                        if (facility.getWard() == null || !facility.getWard().equalsIgnoreCase(ward.trim())) {
                            return false;
                        }
                    }
                    if (status != null && !status.isBlank()) {
                        if (facility.getOperationalStatus() == null || !facility.getOperationalStatus().equalsIgnoreCase(status.trim())) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(FacilityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FacilityResponse getFacilityById(Long id) {
        ToiletFacility facility = getFacilityEntity(id);
        return FacilityResponse.fromEntity(facility);
    }

    @Transactional(readOnly = true)
    public ToiletFacility getFacilityEntity(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Toilet facility not found with ID: " + id));
    }

    public FacilityResponse createFacility(CreateFacilityRequest request) {
        if (request.getFacilityCode() == null || request.getFacilityCode().trim().isEmpty()) {
            throw new BadRequestException("Facility code is required.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Facility name is required.");
        }
        if (request.getWard() == null || request.getWard().trim().isEmpty()) {
            throw new BadRequestException("Ward is required.");
        }

        facilityRepository.findByFacilityCode(request.getFacilityCode().trim()).ifPresent(f -> {
            throw new BadRequestException("Facility with code '" + request.getFacilityCode() + "' already exists.");
        });

        ToiletFacility facility = new ToiletFacility(
                request.getFacilityCode().trim(),
                request.getName().trim(),
                request.getWard().trim(),
                request.getLocationAddress(),
                request.getTotalCabins() > 0 ? request.getTotalCabins() : 4
        );

        ToiletFacility saved = facilityRepository.save(facility);
        return FacilityResponse.fromEntity(saved);
    }
}
