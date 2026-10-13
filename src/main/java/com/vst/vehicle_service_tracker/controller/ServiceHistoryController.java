package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.ServiceHistoryResponse;
import com.vst.vehicle_service_tracker.service.ServiceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-history")
public class ServiceHistoryController {

    private final ServiceHistoryService serviceHistoryService;

    public ServiceHistoryController(ServiceHistoryService serviceHistoryService) {
        this.serviceHistoryService = serviceHistoryService;
    }

    /** Customer views history for their own vehicle */
    @GetMapping("/my/vehicle/{vehicleId}")
    public ResponseEntity<List<ServiceHistoryResponse>> getMyVehicleHistory(@PathVariable Long vehicleId,
                                                                            Authentication auth) {
        return ResponseEntity.ok(
                serviceHistoryService.getMyVehicleHistory(auth.getName(), vehicleId)
        );
    }

    /** Advisor/Admin views history for any vehicle */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ServiceHistoryResponse>> getVehicleHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(serviceHistoryService.getVehicleHistory(vehicleId));
    }
}