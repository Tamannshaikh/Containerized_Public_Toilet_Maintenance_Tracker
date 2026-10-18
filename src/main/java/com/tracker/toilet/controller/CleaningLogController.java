package com.tracker.toilet.controller;

import com.tracker.toilet.dto.CleaningLogResponse;
import com.tracker.toilet.dto.CreateCleaningLogRequest;
import com.tracker.toilet.service.CleaningLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cleaning-logs")
@CrossOrigin(origins = "*")
public class CleaningLogController {

    private final CleaningLogService cleaningLogService;

    public CleaningLogController(CleaningLogService cleaningLogService) {
        this.cleaningLogService = cleaningLogService;
    }

    @PostMapping
    public ResponseEntity<CleaningLogResponse> createCleaningLog(@RequestBody CreateCleaningLogRequest request) {
        CleaningLogResponse response = cleaningLogService.createCleaningLog(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CleaningLogResponse>> getAllCleaningLogs(
            @RequestParam(name = "facilityId", required = false) Long facilityId) {
        List<CleaningLogResponse> logs = cleaningLogService.getAllCleaningLogs(facilityId);
        return ResponseEntity.ok(logs);
    }
}
