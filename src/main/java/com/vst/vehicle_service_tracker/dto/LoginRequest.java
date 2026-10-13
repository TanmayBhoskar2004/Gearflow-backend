package com.vst.vehicle_service_tracker.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}