package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.TechnicianResponse;

import java.util.List;

public interface TechnicianService {
    List<TechnicianResponse> getAllTechnicians();
    List<TechnicianResponse> getAvailableTechnicians();
    TechnicianResponse getTechnicianByUsername(String username);
}