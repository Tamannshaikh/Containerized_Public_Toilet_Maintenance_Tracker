package com.tracker.toilet.controller;

import com.tracker.toilet.dto.CreateFacilityRequest;
import com.tracker.toilet.dto.FacilityResponse;
import com.tracker.toilet.service.FacilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facilities")
@CrossOrigin(origins = "*")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    public ResponseEntity<List<FacilityResponse>> getAllFacilities(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "ward", required = false) String ward,
            @RequestParam(name = "status", required = false) String status) {
        List<FacilityResponse> facilities = facilityService.getAllFacilities(search, ward, status);
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityResponse> getFacilityById(@PathVariable("id") Long id) {
        FacilityResponse facility = facilityService.getFacilityById(id);
        return ResponseEntity.ok(facility);
    }

    @PostMapping
    public ResponseEntity<FacilityResponse> createFacility(@RequestBody CreateFacilityRequest request) {
        FacilityResponse created = facilityService.createFacility(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
