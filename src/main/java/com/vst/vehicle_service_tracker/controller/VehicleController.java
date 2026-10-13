package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.VehicleRequest;
import com.vst.vehicle_service_tracker.dto.VehicleResponse;
import com.vst.vehicle_service_tracker.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /** Customer adds a new vehicle */
    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(Authentication auth,
                                                      @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.addVehicle(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Get all vehicles of the logged-in customer */
    @GetMapping("/my")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(Authentication auth) {
        return ResponseEntity.ok(vehicleService.getMyVehicles(auth.getName()));
    }

    /** Get a specific vehicle by id */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id, auth.getName()));
    }

    /** Update vehicle */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable Long id,
                                                         Authentication auth,
                                                         @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, auth.getName(), request));
    }

    /** Delete vehicle */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id, Authentication auth) {
        vehicleService.deleteVehicle(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    /** Admin/Advisor sees all vehicles */
    @GetMapping("/all")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }
}