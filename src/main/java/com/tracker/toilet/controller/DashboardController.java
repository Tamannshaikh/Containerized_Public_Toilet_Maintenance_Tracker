package com.tracker.toilet.controller;

import com.tracker.toilet.dto.AlertsResponse;
import com.tracker.toilet.dto.SummaryIndicatorsResponse;
import com.tracker.toilet.service.DashboardSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardSummaryService dashboardSummaryService;

    public DashboardController(DashboardSummaryService dashboardSummaryService) {
        this.dashboardSummaryService = dashboardSummaryService;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<SummaryIndicatorsResponse> getSummaryIndicators() {
        SummaryIndicatorsResponse summary = dashboardSummaryService.getSummaryIndicators();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/alerts/exceptions")
    public ResponseEntity<AlertsResponse> getAlertsAndExceptions() {
        AlertsResponse alerts = dashboardSummaryService.getAlertsAndExceptions();
        return ResponseEntity.ok(alerts);
    }
}
