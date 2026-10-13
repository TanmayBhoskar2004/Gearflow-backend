package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.ServiceHistoryResponse;

import java.util.List;

public interface ServiceHistoryService {
    List<ServiceHistoryResponse> getMyVehicleHistory(String username, Long vehicleId);
    List<ServiceHistoryResponse> getVehicleHistory(Long vehicleId);
}