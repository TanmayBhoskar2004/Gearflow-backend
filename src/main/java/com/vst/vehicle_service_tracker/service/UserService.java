package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.RegisterRequest;

public interface UserService {
    void register(RegisterRequest request);
}