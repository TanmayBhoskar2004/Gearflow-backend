package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.VehicleRequest;
import com.vst.vehicle_service_tracker.dto.VehicleResponse;

import java.util.List;

public interface VehicleService {
    VehicleResponse addVehicle(String username, VehicleRequest request);
    List<VehicleResponse> getMyVehicles(String username);
    VehicleResponse getVehicleById(Long id, String username);
    VehicleResponse updateVehicle(Long id, String username, VehicleRequest request);
    void deleteVehicle(Long id, String username);
    List<VehicleResponse> getAllVehicles();
}