package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.ServiceRequestRequest;
import com.vst.vehicle_service_tracker.dto.ServiceRequestResponse;
import com.vst.vehicle_service_tracker.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /** Customer creates a service request */
    @PostMapping
    public ResponseEntity<ServiceRequestResponse> createRequest(Authentication auth,
                                                                @Valid @RequestBody ServiceRequestRequest request) {
        ServiceRequestResponse response = serviceRequestService.createRequest(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Customer views their own requests */
    @GetMapping("/my")
    public ResponseEntity<List<ServiceRequestResponse>> getMyRequests(Authentication auth) {
        return ResponseEntity.ok(serviceRequestService.getMyRequests(auth.getName()));
    }

    /** Advisor/Admin views all requests */
    @GetMapping("/all")
    public ResponseEntity<List<ServiceRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(serviceRequestService.getAllRequests());
    }

    /** Advisor/Admin views only pending requests */
    @GetMapping("/pending")
    public ResponseEntity<List<ServiceRequestResponse>> getPendingRequests() {
        return ResponseEntity.ok(serviceRequestService.getPendingRequests());
    }

    /** Get single request */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRequestService.getRequestById(id));
    }
}