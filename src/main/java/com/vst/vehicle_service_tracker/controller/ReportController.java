package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.ServiceReportResponse;
import com.vst.vehicle_service_tracker.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/services")
    public ResponseEntity<ServiceReportResponse> getServiceReport() {
        return ResponseEntity.ok(reportService.getServiceReport());
    }
}