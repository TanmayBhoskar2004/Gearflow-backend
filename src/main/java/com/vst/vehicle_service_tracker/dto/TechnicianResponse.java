package com.vst.vehicle_service_tracker.dto;

import lombok.Data;

@Data
public class TechnicianResponse {
    private Long id;
    private String username;
    private String email;
    private String specialization;
    private boolean availability;
}