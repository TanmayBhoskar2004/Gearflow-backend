package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.*;
import com.vst.vehicle_service_tracker.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/customer")
    public ResponseEntity<CustomerDashboardResponse> customer(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getCustomerDashboard(auth.getName()));
    }

    @GetMapping("/advisor")
    public ResponseEntity<AdvisorDashboardResponse> advisor() {
        return ResponseEntity.ok(dashboardService.getAdvisorDashboard());
    }

    @GetMapping("/technician")
    public ResponseEntity<TechnicianDashboardResponse> technician(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getTechnicianDashboard(auth.getName()));
    }

    @GetMapping("/inventory")
    public ResponseEntity<InventoryDashboardResponse> inventory() {
        return ResponseEntity.ok(dashboardService.getInventoryDashboard());
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> admin() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }
}